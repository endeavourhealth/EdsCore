package org.endeavourhealth.core.database.dal.admin;

import com.google.common.base.Strings;
import org.endeavourhealth.common.utility.XmlSerializer;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;
import org.endeavourhealth.core.database.dal.admin.models.DefinitionItemType;
import org.endeavourhealth.core.database.dal.admin.models.Item;
import org.endeavourhealth.core.xml.QueryDocument.*;
import org.endeavourhealth.core.xml.QueryDocument.System;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryRepositoryHelper {
	private static final Logger LOG = LoggerFactory.getLogger(LibraryRepositoryHelper.class);

	//disable validation when READING XML (it's still done when writing)
	//private static final String XSD = "QueryDocument.xsd";

	private static final long CACHE_DURATION = 1000 * 60; //cache objects for 60s

	private static final LibraryDalI repository = DalProvider.factoryLibraryDal();

	private static final Map<String, ExpiringCache<TechnicalInterface>> technicalInterfaceCache = new ConcurrentHashMap<>();
	private static final Map<String, ExpiringCache<LibraryItem>> libraryItemCache = new ConcurrentHashMap<>();

	public static List<LibraryItem> getProtocolsByServiceId(String serviceId, String systemId) throws Exception {
		DefinitionItemType itemType = DefinitionItemType.Protocol;

		Iterable<ActiveItem> activeItems = null;
		List<Item> items = new ArrayList();

		activeItems = repository.getActiveItemByTypeId(itemType.getValue(), false);

		for (ActiveItem activeItem: activeItems) {
			Item item = repository.getItemByKey(activeItem.getItemId(), activeItem.getAuditId());
			if (!item.isDeleted()) {
				items.add(item);
			}
		}
		//LOG.trace("Found " + items.size() + " protocols to check for service " + serviceId + " and system " + systemId);

		List<LibraryItem> ret = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			//LOG.trace("Checking protocol " + item.getTitle());

			boolean addItem = false;

			String xml = item.getXmlContent();
			LibraryItem libraryItem = XmlSerializer.deserializeFromString(LibraryItem.class, xml, null);
			//LibraryItem libraryItem = XmlSerializer.deserializeFromString(LibraryItem.class, xml, XSD);
			Protocol protocol = libraryItem.getProtocol();
			List<ServiceContract> serviceContracts = protocol.getServiceContract();
			for (int s = 0; s < serviceContracts.size(); s++) {
				ServiceContract serviceContract = serviceContracts.get(s);

				//filter on service
				String serviceContractServiceId = serviceContract.getService().getUuid();
				//LOG.trace("Check contract service " + serviceContractServiceId);
				if (!serviceContractServiceId.equals(serviceId)) {
					//LOG.trace("Skipping contract");
					continue;
				}

				//filter on optional system
				String serviceContractSystemId = serviceContract.getSystem().getUuid();
				//LOG.trace("Check contract system " + serviceContractSystemId);
				if (!Strings.isNullOrEmpty(systemId)
						&& !serviceContractSystemId.equals(systemId)) {
					//LOG.trace("Skipping contract");
					continue;
				}

				// Load full system details
				String systemUuid = serviceContract.getSystem().getUuid();
				ActiveItem activeSystemItem = repository.getActiveItemByItemId(UUID.fromString(systemUuid));
				Item systemItem = repository.getItemByKey(activeSystemItem.getItemId(), activeSystemItem.getAuditId());
				String systemLibraryItemXml = systemItem.getXmlContent();

				LibraryItem systemLibraryItem = XmlSerializer.deserializeFromString(LibraryItem.class, systemLibraryItemXml, null);
				//LibraryItem systemLibraryItem = XmlSerializer.deserializeFromString(LibraryItem.class, systemLibraryItemXml, XSD);
				System system = systemLibraryItem.getSystem();
				serviceContract.setSystem(system);

				TechnicalInterface technicalInterface = system.getTechnicalInterface().stream()
						.filter(ti -> ti.getUuid().equals(serviceContract.getTechnicalInterface().getUuid()))
						.findFirst()
						.orElse(null);

				if (technicalInterface != null) {
					serviceContract.setTechnicalInterface(technicalInterface);
				}

				//LOG.trace("Will add protocol");
				addItem = true;
				//don't break out, so we continue and populate the technical interface
				//details for any other service contracts for this service
			}

			if (addItem) {
				ret.add(libraryItem);
			}
		}

		return ret;
	}

	public static LibraryItem getLibraryItem(UUID itemUuid) throws Exception {

		ActiveItem activeItem = repository.getActiveItemByItemId(itemUuid);
		Item item = repository.getItemByKey(itemUuid, activeItem.getAuditId());

		String xml = item.getXmlContent();
		LibraryItem libraryItem = XmlSerializer.deserializeFromString(LibraryItem.class, xml, null);
		//LibraryItem libraryItem = XmlSerializer.deserializeFromString(LibraryItem.class, xml, XSD);
		return libraryItem;

	}

	public static TechnicalInterface getTechnicalInterfaceDetails(String systemUuidStr, String technicalInterfaceUuidStr) throws Exception {

		UUID systemUuid = UUID.fromString(systemUuidStr);

		LibraryItem libraryItem = null;
		try {
			libraryItem = LibraryRepositoryHelper.getLibraryItem(systemUuid);
		} catch (Exception e) {
			throw new Exception("Failed to read library item for " + systemUuidStr, e);
		}

		System system = libraryItem.getSystem();
		TechnicalInterface technicalInterface = system
				.getTechnicalInterface()
				.stream()
				.filter(ti -> ti.getUuid().equals(technicalInterfaceUuidStr))
				.findFirst()
				.get();

		return technicalInterface;
	}

	/**
	 * uses a cache to cut load on deserialising Technical Interfaces. Cached TIs only stay in the cache for a minute before
	 * being reloaded from the DB. Would ideally like to use JCS for this caching, but that requires the cached object
	 * implement Serializable, which I don't want to do. So this is a quick alternative.
     */
	public static TechnicalInterface getTechnicalInterfaceDetailsUsingCache(String systemUuidStr, String technicalInterfaceUuidStr) throws Exception {

		String cacheKey = systemUuidStr + ":" + technicalInterfaceUuidStr;
		ExpiringCache<TechnicalInterface> cacheWrapper = technicalInterfaceCache.get(cacheKey);
		if (cacheWrapper == null
				|| cacheWrapper.isExpired()) {

			synchronized (technicalInterfaceCache) {

				//once in the sync block, make another check, in case another thread has refreshed our cache for us
				cacheWrapper = technicalInterfaceCache.get(cacheKey);
				if (cacheWrapper == null
						|| cacheWrapper.isExpired()) {

					TechnicalInterface ti = getTechnicalInterfaceDetails(systemUuidStr, technicalInterfaceUuidStr);
					cacheWrapper = new ExpiringCache<>(ti, CACHE_DURATION);
					technicalInterfaceCache.put(cacheKey, cacheWrapper);
				}
			}
		}
		return cacheWrapper.getObject();
	}

	/**
	 * uses a cache to cut load on deserialising LibraryItems (particularly protocols). Library items
	 * stay in the cache for one minute before expiring.
     */
	public static LibraryItem getLibraryItemUsingCache(UUID itemUuid) throws Exception {
		String cacheKey = itemUuid.toString();
		ExpiringCache<LibraryItem> cacheWrapper = libraryItemCache.get(cacheKey);
		if (cacheWrapper == null
				|| cacheWrapper.isExpired()) {

			synchronized (libraryItemCache) {

				//once in the sync block, make another check, in case another thread has refreshed our cache for us
				cacheWrapper = libraryItemCache.get(cacheKey);
				if (cacheWrapper == null
						|| cacheWrapper.isExpired()) {

					LibraryItem libraryItem = getLibraryItem(itemUuid);
					cacheWrapper = new ExpiringCache<>(libraryItem, CACHE_DURATION);
					libraryItemCache.put(cacheKey, cacheWrapper);
				}
			}
		}
		return cacheWrapper.getObject();
	}
}

/**
 * simple cache wrapper to allow us to expire items in the map
 */
class ExpiringCache<T> {
	private T object;
	private long expiry;

	public ExpiringCache(T object, long msLife) {
		this.object = object;
		this.expiry = java.lang.System.currentTimeMillis() + msLife;
	}

	public boolean isExpired() {
		return java.lang.System.currentTimeMillis() > expiry;
	}

	public T getObject() {
		return object;
	}

	public long getExpiry() {
		return expiry;
	}
}

package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ExchangeGeneralErrorDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeGeneralError;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeGeneralError;
import org.endeavourhealth.dashboardinformation.json.JsonGraphOptions;
import org.endeavourhealth.dashboardinformation.json.JsonGraphResults;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class RdbmsExchangeGeneralErrorDal implements ExchangeGeneralErrorDalI {

    @Override
    public void save(UUID exchangeId, String errorMessage) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_general_error"
                    + " (exchange_id, error_message)"
                    + " VALUES (?, ?)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, exchangeId.toString());
            ps.setString(2, errorMessage);

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<ExchangeGeneralError> getGeneralErrors() throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        LocalDateTime ldt = LocalDateTime.now().minusDays(2);
        Date twoDaysAgo = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeGeneralError c"
                    + " where c.insertedAt > :twoDays  "
                    + " order by c.insertedAt desc ";

            Query query = entityManager.createQuery(sql, RdbmsExchangeGeneralError.class)
                    .setParameter("twoDays", new java.sql.Date(twoDaysAgo.getTime()));


            List<RdbmsExchangeGeneralError> results = query.getResultList();

            //can't use stream() here as the constructor can throw an exception
            List<ExchangeGeneralError> ret = new ArrayList<>();
            for (RdbmsExchangeGeneralError result: results) {
                ret.add(new ExchangeGeneralError(result));
            }
            return ret;

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteExchangeGeneralError(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        /*RdbmsQueuedMessage dbObj = new RdbmsQueuedMessage();
        dbObj.setId(id.toString());*/

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support deletes without retrieving first
            //entityManager.remove(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM exchange_general_error"
                    + " WHERE id = ?";

            ps = connection.prepareStatement(sql);

            ps.setString(1, exchangeId.toString());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<JsonGraphResults> getGraphData(JsonGraphOptions options) throws Exception {
        List<JsonGraphResults> results = new ArrayList<>();

        String tableGUID = UUID.randomUUID().toString().replace("-", "");

        initialiseReportResultTable(options, tableGUID);

        results.add(createGraphResults("General Errors", "exchange_general_error", options.getPeriod(), tableGUID));

        results.add(createGraphResults("Protocol Errors", "exchange_protocol_error", options.getPeriod(), tableGUID));

        deleteDateRangeTable(tableGUID);

        return results;
    }

    private JsonGraphResults createGraphResults(String title, String errorTable, String period, String tableGUID) throws Exception {
        JsonGraphResults graph = new JsonGraphResults();
        graph.setTitle(title);
        graph.setResults(getGraphValues(period, tableGUID, errorTable));

        return graph;
    }

    private static void initialiseReportResultTable(JsonGraphOptions options, String tableGUID) throws Exception {
        List<Object> params = new ArrayList<>();
        String startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(options.getStartTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(options.getEndTime());

        String insert = "create table audit.graph_date_range_" + tableGUID + " \n" +
                "select a.Date as reference_date\n" +
                "from (\n" +
                "    select " + parameterize(params, endDate) + " - INTERVAL (a.a + (10 * b.a) + (100 * c.a)) " + parameterize(params, options.getPeriod()) + " as Date\n" +
                "    from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a\n" +
                "    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b\n" +
                "    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c\n" +
                ") a\n" +
                "where a.Date between " + parameterize(params, startDate)+ " and " + parameterize(params, endDate);

        runSQLScript(insert, params);

    }

    private static void deleteDateRangeTable(String tableGUID) throws Exception {

        String delete = "drop table audit.graph_date_range_" + tableGUID;

        runSQLScript(delete, null);
    }

    public static List getGraphValues(String period, String tableGUID, String errorTable) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        List<Object> params = new ArrayList<>();

        String sql = "";

        switch (period) {
            case "YEAR":
                sql = getYearSQLScript(tableGUID, errorTable, params);
                break;
            case "MONTH":
                sql = getMonthSQLScript(tableGUID, errorTable, params);
                break;
            case "DAY":
                sql = getDaySQLScript(tableGUID, errorTable, params);
                break;
            case "HOUR":
                sql = getHourSQLScript(tableGUID, errorTable, params);
                break;
        }

        Query q = entityManager.createNativeQuery(sql);

        if (params != null && !params.isEmpty())
            setQueryParams(q, params);

        List resultList =  q.getResultList();

        entityManager.close();

        return resultList;
    }

    private static String getHourSQLScript(String tableGUID, String errorTable, List<Object> params) throws Exception {


        return "select DATE_FORMAT(r.reference_date, \"%%d/%%m/%%Y %%H\"), count(e.exchange_id)  \n" +
                " from audit.graph_date_range_" + tableGUID + " r \n" +
                " left outer join audit." + parameterize(params, errorTable) + " e    \n" +
                " on HOUR(e.inserted_at) = HOUR(r.reference_date)   \n" +
                " and DATE(e.inserted_at) = DATE(r.reference_date)   \n" +
                " group by DATE(r.reference_date), HOUR(r.reference_date)";
    }

    private static String getYearSQLScript(String tableGUID, String errorTable, List<Object> params) throws Exception {

        return "select DATE_FORMAT(r.reference_date, \"%%d/%%m/%%Y\"), count(e.exchange_id)  \n" +
                " from audit.graph_date_range_" + tableGUID + " r \n" +
                " left outer join audit." + parameterize(params, errorTable) + " e    \n" +
                " on YEAR(e.inserted_at) = YEAR(r.reference_date)   \n" +
                " group by YEAR(r.reference_date)";
    }

    private static String getMonthSQLScript(String tableGUID, String errorTable, List<Object> params) throws Exception {

        return "select DATE_FORMAT(r.reference_date, \"%%d/%%m/%%Y %%H\"), count(e.exchange_id)  \n" +
                " from audit.graph_date_range_" + tableGUID + " r \n" +
                " left outer join audit." + parameterize(params, errorTable) + " e    \n" +
                " on MONTH(e.inserted_at) = MONTH(r.reference_date)   \n" +
                " and YEAR(e.inserted_at) = YEAR(r.reference_date)   \n" +
                " group by MONTH(r.reference_date), YEAR(r.reference_date)";
    }

    private static String getDaySQLScript(String tableGUID, String errorTable, List<Object> params) throws Exception {

        return "select DATE_FORMAT(r.reference_date, \"%%d/%%m/%%Y\"), count(e.exchange_id)  \n" +
                " from audit.graph_date_range_" + tableGUID + " r \n" +
                " left outer join audit." + parameterize(params, errorTable) + " e    \n" +
                " on DATE(e.inserted_at) = DATE(r.reference_date)   \n" +
                " group by DATE(r.reference_date)";
    }

    private static int runSQLScript(String script, List<Object> params) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        entityManager.getTransaction().begin();
        try {
            Query q = entityManager.createNativeQuery(script);

            if (params != null && !params.isEmpty())
                setQueryParams(q, params);

            int ret = q.executeUpdate();

            entityManager.getTransaction().commit();

            return ret;

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    private static String parameterize(List<Object> list, Object value) {
        list.add(value);
        return " ?" + list.size() + " ";
    }

    public static void setQueryParams(Query q, List<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i) instanceof Calendar)
                q.setParameter(i + 1, (Calendar) params.get(i), TemporalType.TIMESTAMP);
            else
                q.setParameter(i + 1, params.get(i));
        }
    }
}

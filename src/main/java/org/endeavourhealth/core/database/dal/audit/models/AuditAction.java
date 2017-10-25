package org.endeavourhealth.core.database.dal.audit.models;

import java.util.ArrayList;
import java.util.List;

public enum AuditAction {
	Load,
	Save,
	Delete,
	Move,
	Run;

	public static List<String> getActionList() {
		List<String> actions = new ArrayList<>();
		for (AuditAction action : AuditAction.values()) {
			actions.add(action.name());
		}
		return actions;
	}
}

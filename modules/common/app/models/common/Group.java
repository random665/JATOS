package models.common;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import models.common.workers.JatosWorker;
import models.common.workers.PersonalMultipleWorker;
import models.common.workers.PersonalSingleWorker;

/**
 * Model of a DB entity of a group with all properties of a group (without the
 * results). The results are stored in GroupResults. Default values, where
 * necessary, are at the fields or the constructor.
 * 
 * We can't use 'Group' for the database table name, since it's a keyword in
 * some databases (therfore the 'Groupp').
 * 
 * @author Kristian Lange (2015)
 */
@Entity
@Table(name = "Groupp")
public class Group {

	public static final String MIN_ACTIVE_MEMBER_SIZE = "minActiveMemberSize";
	public static final String MAX_ACTIVE_MEMBER_SIZE = "maxActiveMemberSize";
	public static final String MAX_TOTAL_MEMBER_SIZE = "maxTotalMemberSize";
	public static final String MESSAGING = "messaging";
	public static final String ALLOWED_WORKER_TYPES = "allowedWorkerTypes";

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Is this group allowed to send messages between each other with the
	 * GroupMessagingService.
	 */
	private boolean messaging = false;

	/**
	 * Minimum number of workers in the group that are active at the same time.
	 */
	private int minActiveMemberSize = 2;

	/**
	 * Maximum number of workers in the group that are active at the same time.
	 * If there is no limit in active members the value is null.
	 */
	private Integer maxActiveMemberSize = null;

	/**
	 * Maximum number of workers in total. If there is no limit in active
	 * members the value is null.
	 */
	private Integer maxTotalMemberSize = null;

	/**
	 * List of worker types that are allowed to run this study. If the worker
	 * type is not in this list, it has no permission to run this study.
	 */
	@ElementCollection
	private Set<String> allowedWorkerTypes = new HashSet<>();

	public Group() {
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public boolean isMessaging() {
		return messaging;
	}

	public void setMessaging(boolean messaging) {
		this.messaging = messaging;
	}

	public int getMinActiveMemberSize() {
		return minActiveMemberSize;
	}

	public void setMinActiveMemberSize(int minActiveMemberSize) {
		this.minActiveMemberSize = minActiveMemberSize;
	}

	public Integer getMaxActiveMemberSize() {
		return maxActiveMemberSize;
	}

	public void setMaxActiveMemberSize(Integer maxActiveMemberSize) {
		this.maxActiveMemberSize = maxActiveMemberSize;
	}

	public Integer getMaxTotalMemberSize() {
		return maxTotalMemberSize;
	}

	public void setMaxTotalMemberSize(Integer maxTotalMemberSize) {
		this.maxTotalMemberSize = maxTotalMemberSize;
	}

	public void setAllowedWorkerTypes(Set<String> allowedWorkerTypes) {
		this.allowedWorkerTypes = allowedWorkerTypes;
	}

	public Set<String> getAllowedWorkerTypes() {
		return this.allowedWorkerTypes;
	}

	public void addAllowedWorkerType(String workerType) {
		allowedWorkerTypes.add(workerType);
	}

	public void removeAllowedWorkerType(String workerType) {
		allowedWorkerTypes.remove(workerType);
	}

	public boolean hasAllowedWorkerType(String workerType) {
		return allowedWorkerTypes.contains(workerType);
	}

	/**
	 * Add default allowed workers
	 */
	public void initStudyProperties() {
		addAllowedWorkerType(JatosWorker.WORKER_TYPE);
		addAllowedWorkerType(PersonalMultipleWorker.WORKER_TYPE);
		addAllowedWorkerType(PersonalSingleWorker.WORKER_TYPE);
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Group)) {
			return false;
		}
		Group other = (Group) obj;
		if (id == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!id.equals(other.getId())) {
			return false;
		}
		return true;
	}

}

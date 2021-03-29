package pers.clare.demo.data.entity;

import lombok.*;

import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * The primary key class for the AccessLog database table.
 * 
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Long id;

	private Long time;
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AccessLogPK)) {
			return false;
		}
		AccessLogPK castOther = (AccessLogPK)other;
		return 
			this.id.equals(castOther.id)
			&& this.time.equals(castOther.time);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.id.hashCode();
		hash = hash * prime + this.time.hashCode();
		
		return hash;
	}
}

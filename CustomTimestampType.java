package com.junz.hibernate.domain;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
 
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import com.junz.hibernate.time.TimeUtil;

public class CustomTimestampType implements UserType{

  public int[] sqlTypes() {
		 return new int[]{Types.TIMESTAMP};
	}
 
	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return Timestamp.class;
	}
 
	public boolean equals(Object x, Object y) throws HibernateException {
		 return x == y || !(x == null || y == null) && x.equals(y);
	}
 
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}
 
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		
		Timestamp time = rs.getTimestamp(names[0]);
		
        if (rs.wasNull()) {
            return null;
        }       
		
        System.out.println("get time after db----" + time + "," + time.getTime());
        time = new Timestamp(TimeUtil.createUnixTimeNumber(time, "GMT"));
        System.out.println("get time before db----" + time + "," + time.getTime());
		
        return new Timestamp(time.getTime());
	}
 
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.TIMESTAMP);
        }
        else {
            Timestamp time = (Timestamp) value;
            
            System.out.println("set time before db----" + time + "," + time.getTime());            
            time = TimeUtil.createTimeRepresentation(time.getTime(), "GMT");
            System.out.println("set time after db----" + time + "," + time.getTime());
            
            st.setTimestamp(index, time);
        }
	}
 
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}
 
	public boolean isMutable() {
		return false;
	}
 
	public Serializable disassemble(Object value) throws HibernateException {
		 return (Serializable) value;
	}
 
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}
}

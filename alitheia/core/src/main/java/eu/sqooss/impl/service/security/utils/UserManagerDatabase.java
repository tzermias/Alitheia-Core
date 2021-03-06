/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.security.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.PendingUser;
import eu.sqooss.service.db.User;

public class UserManagerDatabase implements UserManagerDBQueries {

    private static final String ATTRIBUTE_USER_NAME = "name";
    
    private DBService db;
    private Map<String, Object> userProps;
    private Object lockObject = new Object();
    
    public UserManagerDatabase(DBService db) {
        super();
        this.db = db;
        userProps = new Hashtable<String, Object>(1);
    }

    public User getUser(long userId) {
    	try {
	    	if(db.startDBSession()) {
	    		return db.findObjectById(User.class, userId);
	    	}
	    	else
	    		return null;
    	}
    	finally {
    		if(db.isDBSessionActive())
    			db.commitDBSession();
    	}
    }
    
	public List<User> getUser(String userName) {
		try {
			if (db.startDBSession()) {
				synchronized (lockObject) {
					userProps.clear();
					userProps.put(ATTRIBUTE_USER_NAME, userName);
					return db.findObjectsByProperties(User.class, userProps);
				}
			} else
				return null;
		} finally {
			if (db.isDBSessionActive())
				db.commitDBSession();
		}
	}
    
    public List<?> getUsers() {
    	try {
	    	if(db.startDBSession()) {
	    		return db.doHQL(GET_USERS);
	    	}
	    	else
	    		return Collections.emptyList();
    	}
    	finally {
    		if(db.isDBSessionActive())
    			db.commitDBSession();
    	}
    }
    
	public Set<?> getUsers(long groupId) {
		try {
			if (db.startDBSession()) {
				Group group = db.findObjectById(Group.class, groupId);
				if (group != null) {
					return group.getUsers();
				} else {
					return null;
				}
			} else
				return null;
		} finally {
			if (db.isDBSessionActive())
				db.commitDBSession();
		}
	}
    
    public boolean createUser(User newUser) {
        if(db != null && db.startDBSession())
    	{
    		if(db.addRecord(newUser)) 
    			return db.commitDBSession();
    	}
    	return false;
    }

    public boolean createPendingUser(PendingUser newPendingUser) {
    	if(db != null && db.startDBSession())
    	{
    		if(db.addRecord(newPendingUser)) 
    			return db.commitDBSession();
    	}
    	return false;
    }
    
	public boolean modifyUser(String userName, String newPasswordHash,
			String newEmail) {
		List<User> users = null;
		boolean isModified = false;
		try {
			if (db != null && db.startDBSession()) {

				synchronized (lockObject) {
					userProps.clear();
					userProps.put(ATTRIBUTE_USER_NAME, userName);
					users = db.findObjectsByProperties(User.class, userProps);
				}
				if (users.size() == 1) {
					User user = users.get(0);
					if (newPasswordHash != null) {
						user.setPassword(newPasswordHash);
						isModified = true;
					}
					if (newEmail != null) {
						user.setEmail(newEmail);
						isModified = true;
					}
				}
			} else
				isModified = false;
		} finally {
			if (db.isDBSessionActive())
				db.commitDBSession();
		}
		return isModified;
	}
    
    public boolean deleteUser(User user) {
    	if(db != null && db.startDBSession())
    	{
    		if(db.deleteRecord(user)) 
    			return db.commitDBSession();
    	}
    	return false;
    }

    public boolean hasPendingUserHash (String hashValue) {
        if (getPendingUser("hash", hashValue) != null) return true;
        return false;
    }

    public boolean hasPendingUserName(String userName) {
        if (getPendingUser("name", userName) != null) return true;
        return false;
    }

	public PendingUser getPendingUser(String field, String value) {
		try {
			if (db.startDBSession()) {
				// Search for a matching pending user's record
				HashMap<String, Object> filter = new HashMap<String, Object>();
				filter.put(field, value);
				List<PendingUser> pending = db.findObjectsByProperties(
						PendingUser.class, filter);

				if (!pending.isEmpty()) {
					return pending.get(0);
				}
				return null;
			} else
				return null;
		} finally {
			if (db.isDBSessionActive())
				db.commitDBSession();
		}
	}

    public List<?> getFirstPendingUser() {
        try {
	    	if(db.startDBSession()) {
	    		return db.doHQL(GET_FIRST_PENDING_USER);
	    	}
	    	else
	    		return Collections.emptyList();
    	}
    	finally {
    		if(db.isDBSessionActive())
    			db.commitDBSession();
    	}
    }
    
    public boolean deletePendingUser (PendingUser pending) {
    	if(db != null && db.startDBSession())
    	{
    		if(db.deleteRecord(pending)) 
    			return db.commitDBSession();
    	}
    	return false;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab

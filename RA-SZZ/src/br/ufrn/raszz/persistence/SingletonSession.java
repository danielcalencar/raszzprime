package br.ufrn.raszz.persistence;

import org.hibernate.Session;
import org.hibernate.StatelessSession;

public class SingletonSession 
{
	
	private static Session hibernateSession; 
	private static StatelessSession stateless;
	
	public static Session getSession(String configName) {
		if(hibernateSession != null) {
			return hibernateSession;
		}
		else {
			hibernateSession = HibernateUtil.getMySqlSessionFactory(configName).openSession();
			return hibernateSession;
		}
	}

	public static StatelessSession getStatelessSession(String configName) {
		if(stateless != null) {
			return stateless;
		}
		else {
			stateless = HibernateUtil.getMySqlSessionFactory(configName).openStatelessSession();
			return stateless;
		}
	}
}

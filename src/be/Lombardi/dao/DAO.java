package be.Lombardi.dao;

import java.sql.Connection;

public abstract class DAO<T> {
    protected Connection connect = null;

    public DAO(Connection conn) {
        this.connect = conn;
    }

    public abstract boolean create(T obj) throws DAOException;
    
    public abstract boolean delete(T obj) throws DAOException;
    
    public abstract boolean update(T obj) throws DAOException;
    
    public abstract T find(int id) throws DAOException;
}
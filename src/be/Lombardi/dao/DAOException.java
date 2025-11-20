package be.Lombardi.dao;

/**
 * Exception personnalisée pour les erreurs DAO
 * Masque les détails techniques à l'utilisateur
 */
public class DAOException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7232089111614999480L;

	public DAOException(String message) {
        super(message);
    }
    
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    public String getUserMessage() {
        return "Une erreur s'est produite lors de l'accès à la base de données. Veuillez réessayer.";
    }
    
    public String getTechnicalMessage() {
        return getMessage();
    }
}
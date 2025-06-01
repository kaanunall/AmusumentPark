package application;

public class LoginData {
    private static LoginData instance;
    private String userTC;
    
    private LoginData() {}
    
    public static LoginData getInstance() {
        if (instance == null) {
            instance = new LoginData();
        }
        return instance;
    }
    
    public void setUserTC(String tc) {
        this.userTC = tc;
        System.out.println("TC set to: " + tc);
    }
    
    public String getUserTC() {
        System.out.println("Getting TC: " + userTC);
        return userTC;
    }
} 
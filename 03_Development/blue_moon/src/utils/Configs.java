package utils;

public class Configs {
	public static final String LOGIN_PAGE_PATH = "/views/login/LoginPage.fxml";
	public static final String LOGO_PATH = "/assets/images/logo-2.png";
	public static final String ACCOUNTANT_HOME_PAGE_PATH = "/views/homepage/AccountantHomePage.fxml";
	public static final String LEADER_HOME_PAGE_PATH = "/views/homepage/LeaderHomePage.fxml";
	
	
	public static final String ADD_HOUSEHOLD_DIALOG_PATH = "/views/household/AddHouseholdDialog.fxml";
	public static final String VIEW_HOUSEHOLD_DIALOG_PATH = "/views/household/ViewHouseholdDialog.fxml";
	public static final String RESIDENT_DETAILS_DIALOG_PATH = "/views/household/ResidentDetailsDialog.fxml";
	public static final String TEMPORARY_RESIDENCE_ABSENCE_PATH = "/views/tempresidenceabsence/TemporaryResidenceAbsencePage.fxml";
	
	public static final String FEE_LIST_PATH = "/views/fee/FeeList.fxml";
	public static final String ADD_FEE_FORM_PATH = "/views/fee/AddFeeForm.fxml";
	public static final String UPDATE_FEE_FORM_PATH = "/views/fee/UpdateFeeForm.fxml";
	public static final String CAMPAIGN_FEE_LIST_PATH = "/views/campaignfee/CampaignFeeList.fxml";
	public static final String HOMEPAGE_LOGO_PATH = "/assets/images/logo.png";
	public static final String NEW_CAMPAIGN_FORM = "/views/campaignfee/NewCampaignFeeForm.fxml";
	public static final String UPDATE_CAMPAIGN_FORM = "/views/campaignfee/UpdateCampaignFeeForm.fxml";
	public static final String CHARGE_FEE_SCREEN = "/views/chargefee/ChargeFeeScreen.fxml";
	public static final String UPDATE_AMOUNT_BOX = "/views/chargefee/UpdateChargeFeeScreen.fxml";
	public static final String TRACK_CAMPAIGN_FEE_SCREEN = "/views/trackcampaignfee/TrackCampaignFeeScreen.fxml";
	public static final String[] DAY = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
			                            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", 
			                            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
	public static final String[] MONTH = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
	public static final String[] YEAR = {"2024", "2025", "2026"};
	public static final String[] STATUS = {"MÃ¡Â»â€ºi tÃ¡ÂºÂ¡o", "Ã„ï¿½ang diÃ¡Â»â€¦n ra", "Ã„ï¿½ÃƒÂ£ kÃ¡ÂºÂ¿t thÃƒÂºc"};
	
	
	public static final String NEW_RESIDENT_FORM = "/views/resident/NewResidentForm.fxml";
	public static final String RESIDENT_EDIT_FORM = "/views/resident/ResidentEditForm.fxml";
	public static final String RESIDENT_VIEW_FORM = "/views/resident/ResidentViewForm.fxml";
	
	//public static final String CITIZEN_MANAGEMENT_PAGE_PATH = "/views/citizen/CitizenManagementPage.fxml";
	public static final String RESIDENT_LIST_PATH = "/views/resident/ResidentList.fxml";
	
	public static final String[] GENDER = {"Nam", "NÃ¡Â»Â¯", "KhÃƒÂ¡c"};
	public static final String[] ETHNICITY = {
		    "Kinh", "TÃƒÂ y", "ThÃƒÂ¡i", "H'MÃƒÂ´ng", "MÃ†Â°Ã¡Â»ï¿½ng", "Khmer", "Hoa", "NÃƒÂ¹ng", "Dao", 
		    "Gia Rai", "ÃƒÅ  Ã„ï¿½ÃƒÂª", "Ba Na", "XÃ†Â¡ Ã„ï¿½Ã„Æ’ng", "SÃƒÂ¡n Chay", "CÃ†Â¡ Ho", "ChÃ„Æ’m", 
		    "SÃƒÂ¡n DÃƒÂ¬u", "HrÃƒÂª", "Ra Glai", "MnÃƒÂ´ng", "ThÃ¡Â»â€¢", "XtiÃƒÂªng", "KhÃ†Â¡ MÃƒÂº", "Bru-VÃƒÂ¢n KiÃ¡Â»ï¿½u", 
		    "GiÃƒÂ¡y", "CÃ†Â¡ Tu", "GiÃ¡ÂºÂ» TriÃƒÂªng", "TÃƒÂ  Ãƒâ€�i", "MÃ¡ÂºÂ¡", "Co", "ChÃ†Â¡ Ro", "Xinh Mun", 
		    "HÃƒÂ  NhÃƒÂ¬", "Chu Ru", "LÃƒÂ o", "La ChÃƒÂ­", "La Ha", "PhÃƒÂ¹ LÃƒÂ¡", "LÃ¡Â»Â±", "LÃƒÂ´ LÃƒÂ´", 
		    "ChÃ¡Â»Â©t", "MÃ¡ÂºÂ£ng", "PÃƒÂ  ThÃ¡ÂºÂ»n", "CÃ†Â¡ Lao", "CÃ¡Â»â€˜ng", "BÃ¡Â»â€˜ Y", "La HÃ¡Â»Â§", "NgÃƒÂ¡i", 
		    "Si La", "Pu PÃƒÂ©o", "RÃ†Â¡ MÃ„Æ’m", "BrÃƒÂ¢u", "Ã†Â  Ã„ï¿½u"
		};
	public static final String[] PLACEOFISSUE = {
		    "HÃƒÂ  NÃ¡Â»â„¢i", "HÃ¡Â»â€œ ChÃƒÂ­ Minh", "HÃ¡ÂºÂ£i PhÃƒÂ²ng", "Ã„ï¿½ÃƒÂ  NÃ¡ÂºÂµng", "CÃ¡ÂºÂ§n ThÃ†Â¡", 
		    "An Giang", "BÃƒÂ  RÃ¡Â»â€¹a - VÃ…Â©ng TÃƒÂ u", "BÃ¡ÂºÂ¯c Giang", "BÃ¡ÂºÂ¯c KÃ¡ÂºÂ¡n", "BÃ¡ÂºÂ¡c LiÃƒÂªu", 
		    "BÃ¡ÂºÂ¯c Ninh", "BÃ¡ÂºÂ¿n Tre", "BÃƒÂ¬nh Ã„ï¿½Ã¡Â»â€¹nh", "BÃƒÂ¬nh DÃ†Â°Ã†Â¡ng", "BÃƒÂ¬nh PhÃ†Â°Ã¡Â»â€ºc", 
		    "BÃƒÂ¬nh ThuÃ¡ÂºÂ­n", "CÃƒÂ  Mau", "Cao BÃ¡ÂºÂ±ng", "Ã„ï¿½Ã¡ÂºÂ¯k LÃ¡ÂºÂ¯k", "Ã„ï¿½Ã¡ÂºÂ¯k NÃƒÂ´ng", 
		    "Ã„ï¿½iÃ¡Â»â€¡n BiÃƒÂªn", "Ã„ï¿½Ã¡Â»â€œng Nai", "Ã„ï¿½Ã¡Â»â€œng ThÃƒÂ¡p", "Gia Lai", "HÃƒÂ  Giang", 
		    "HÃƒÂ  Nam", "HÃƒÂ  TÃ„Â©nh", "HÃ¡ÂºÂ£i DÃ†Â°Ã†Â¡ng", "HÃ¡ÂºÂ­u Giang", "HÃƒÂ²a BÃƒÂ¬nh", 
		    "HÃ†Â°ng YÃƒÂªn", "KhÃƒÂ¡nh HÃƒÂ²a", "KiÃƒÂªn Giang", "Kon Tum", "Lai ChÃƒÂ¢u", 
		    "LÃƒÂ¢m Ã„ï¿½Ã¡Â»â€œng", "LÃ¡ÂºÂ¡ng SÃ†Â¡n", "LÃƒÂ o Cai", "Long An", "Nam Ã„ï¿½Ã¡Â»â€¹nh", 
		    "NghÃ¡Â»â€¡ An", "Ninh BÃƒÂ¬nh", "Ninh ThuÃ¡ÂºÂ­n", "PhÃƒÂº ThÃ¡Â»ï¿½", "PhÃƒÂº YÃƒÂªn", 
		    "QuÃ¡ÂºÂ£ng BÃƒÂ¬nh", "QuÃ¡ÂºÂ£ng Nam", "QuÃ¡ÂºÂ£ng NgÃƒÂ£i", "QuÃ¡ÂºÂ£ng Ninh", "QuÃ¡ÂºÂ£ng TrÃ¡Â»â€¹", 
		    "SÃƒÂ³c TrÃ„Æ’ng", "SÃ†Â¡n La", "TÃƒÂ¢y Ninh", "ThÃƒÂ¡i BÃƒÂ¬nh", "ThÃƒÂ¡i NguyÃƒÂªn", 
		    "Thanh HÃƒÂ³a", "ThÃ¡Â»Â«a ThiÃƒÂªn HuÃ¡ÂºÂ¿", "TiÃ¡Â»ï¿½n Giang", "TrÃƒÂ  Vinh", "TuyÃƒÂªn Quang", 
		    "VÃ„Â©nh Long", "VÃ„Â©nh PhÃƒÂºc", "YÃƒÂªn BÃƒÂ¡i"
		};


}

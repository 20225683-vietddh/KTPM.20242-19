package utils;

public class Configs {
	public static final String LOGIN_PAGE_PATH = "/views/login/LoginPage.fxml";
	public static final String LOGO_PATH = "/assets/images/logo-2.png";
	public static final String ACCOUNTANT_HOME_PAGE_PATH = "/views/homepage/AccountantHomePage.fxml";
	public static final String LEADER_HOME_PAGE_PATH = "/views/homepage/LeaderHomePage.fxml";
	
	
	public static final String ADD_HOUSEHOLD_DIALOG_PATH = "/views/household/AddHouseholdDialog.fxml";
	public static final String VIEW_HOUSEHOLD_DIALOG_PATH = "/views/household/ViewHouseholdDialog.fxml";
	public static final String MEMBER_DETAILS_DIALOG_PATH = "/views/household/MemberDetailsDialog.fxml";
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
	public static final String[] STATUS = {"Má»›i táº¡o", "Ä�ang diá»…n ra", "Ä�Ã£ káº¿t thÃºc"};
	
	
	public static final String NEW_RESIDENT_FORM = "/views/resident/NewResidentForm.fxml";
	public static final String RESIDENT_EDIT_FORM = "/views/resident/ResidentEditForm.fxml";
	
	//public static final String CITIZEN_MANAGEMENT_PAGE_PATH = "/views/citizen/CitizenManagementPage.fxml";
	public static final String RESIDENT_LIST_PATH = "/views/resident/ResidentList.fxml";
	
	public static final String[] GENDER = {"Nam", "Ná»¯", "KhÃ¡c"};
	public static final String[] ETHNICITY = {
		    "Kinh", "TÃ y", "ThÃ¡i", "H'MÃ´ng", "MÆ°á»�ng", "Khmer", "Hoa", "NÃ¹ng", "Dao", 
		    "Gia Rai", "ÃŠ Ä�Ãª", "Ba Na", "XÆ¡ Ä�Äƒng", "SÃ¡n Chay", "CÆ¡ Ho", "ChÄƒm", 
		    "SÃ¡n DÃ¬u", "HrÃª", "Ra Glai", "MnÃ´ng", "Thá»•", "XtiÃªng", "KhÆ¡ MÃº", "Bru-VÃ¢n Kiá»�u", 
		    "GiÃ¡y", "CÆ¡ Tu", "Giáº» TriÃªng", "TÃ  Ã”i", "Máº¡", "Co", "ChÆ¡ Ro", "Xinh Mun", 
		    "HÃ  NhÃ¬", "Chu Ru", "LÃ o", "La ChÃ­", "La Ha", "PhÃ¹ LÃ¡", "Lá»±", "LÃ´ LÃ´", 
		    "Chá»©t", "Máº£ng", "PÃ  Tháº»n", "CÆ¡ Lao", "Cá»‘ng", "Bá»‘ Y", "La Há»§", "NgÃ¡i", 
		    "Si La", "Pu PÃ©o", "RÆ¡ MÄƒm", "BrÃ¢u", "Æ  Ä�u"
		};
	public static final String[] PLACEOFISSUE = {
		    "HÃ  Ná»™i", "Há»“ ChÃ­ Minh", "Háº£i PhÃ²ng", "Ä�Ã  Náºµng", "Cáº§n ThÆ¡", 
		    "An Giang", "BÃ  Rá»‹a - VÅ©ng TÃ u", "Báº¯c Giang", "Báº¯c Káº¡n", "Báº¡c LiÃªu", 
		    "Báº¯c Ninh", "Báº¿n Tre", "BÃ¬nh Ä�á»‹nh", "BÃ¬nh DÆ°Æ¡ng", "BÃ¬nh PhÆ°á»›c", 
		    "BÃ¬nh Thuáº­n", "CÃ  Mau", "Cao Báº±ng", "Ä�áº¯k Láº¯k", "Ä�áº¯k NÃ´ng", 
		    "Ä�iá»‡n BiÃªn", "Ä�á»“ng Nai", "Ä�á»“ng ThÃ¡p", "Gia Lai", "HÃ  Giang", 
		    "HÃ  Nam", "HÃ  TÄ©nh", "Háº£i DÆ°Æ¡ng", "Háº­u Giang", "HÃ²a BÃ¬nh", 
		    "HÆ°ng YÃªn", "KhÃ¡nh HÃ²a", "KiÃªn Giang", "Kon Tum", "Lai ChÃ¢u", 
		    "LÃ¢m Ä�á»“ng", "Láº¡ng SÆ¡n", "LÃ o Cai", "Long An", "Nam Ä�á»‹nh", 
		    "Nghá»‡ An", "Ninh BÃ¬nh", "Ninh Thuáº­n", "PhÃº Thá»�", "PhÃº YÃªn", 
		    "Quáº£ng BÃ¬nh", "Quáº£ng Nam", "Quáº£ng NgÃ£i", "Quáº£ng Ninh", "Quáº£ng Trá»‹", 
		    "SÃ³c TrÄƒng", "SÆ¡n La", "TÃ¢y Ninh", "ThÃ¡i BÃ¬nh", "ThÃ¡i NguyÃªn", 
		    "Thanh HÃ³a", "Thá»«a ThiÃªn Huáº¿", "Tiá»�n Giang", "TrÃ  Vinh", "TuyÃªn Quang", 
		    "VÄ©nh Long", "VÄ©nh PhÃºc", "YÃªn BÃ¡i"
		};


}

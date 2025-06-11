package utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Household;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import services.room.RoomService;
import services.room.RoomServiceImpl;
import views.household.AddHouseholdDialogHandler;
import views.household.ViewHouseholdDialogHandler;
import utils.Configs;

import java.io.IOException;

import controllers.household.HouseholdController;
import controllers.resident.ResidentController;
import javafx.scene.control.Button;
import views.resident.AddResidentDialogHandler;
import models.Resident;

/**
 * Utility class for managing JavaFX scenes and navigation between screens.
 */
public class SceneUtils {

    /**
     * Navigates to the main dashboard.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToMainDashboard(ActionEvent event) {
        try {
            loadNewScene(event, "/view/MainDashboard.fxml", "Quản lý hộ khẩu - Bảng điều khiển");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở trang chủ", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the household list view.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToHouseholdList(ActionEvent event) {
        try {
            loadNewScene(event, Configs.HOUSEHOLD_LIST_PATH,"Quản lý hộ khẩu - Danh sách hộ khẩu");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở danh sách hộ khẩu", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the resident list view.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToResidentList(ActionEvent event) {
        try {
            loadNewScene(event, Configs.RESIDENT_LIST_PATH, "Quản lý hộ khẩu - Danh sách nhân khẩu");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở danh sách nhân khẩu", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the temporary residence and absence view.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToTemporaryResidenceView(ActionEvent event) {
        try {
            loadNewScene(event, "/view/TemporaryResidence.fxml", "Quản lý hộ khẩu - Tạm trú tạm vắng");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở trang tạm trú tạm vắng", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the statistics view.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToStatisticsView(ActionEvent event) {
        try {
            loadNewScene(event, "/view/Statistics.fxml", "Quản lý hộ khẩu - Thống kê");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở trang thống kê", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the login screen.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToLoginScreen(ActionEvent event) {
        try {
            loadNewScene(event, "/view/Login.fxml", "Quản lý hộ khẩu - Đăng nhập");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở trang đăng nhập", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the leader home page.
     *
     * @param event The event that triggered the navigation
     */
    public static void navigateToLeaderHomePage(ActionEvent event) {
        try {
            loadNewScene(event, Configs.HOUSEHOLD_LIST_PATH, "Quản lý hộ khẩu - Trang chủ");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở trang chủ", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens the add household form as a modal dialog.
     *
     * @param sourceNode The node that triggered the action
     * @param onSuccess  Callback to execute when household is successfully added
     * @param memberService 
     * @throws IOException If the FXML file cannot be loaded
     */
    public static void openAddHouseholdDialog(HouseholdController householdController, 
    		ResidentService memberService, 
    		RoomService roomService,
    		Node sourceNode, 
    		Runnable onSuccess) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(Configs.ADD_HOUSEHOLD_DIALOG_PATH));
        Parent root = loader.load();

        // Get the controller, set the household and callback
        AddHouseholdDialogHandler controller = loader.getController();
        controller.setHouseholdController(householdController);
        controller.setResidentService(memberService);
        controller.setRoomService(roomService);
        
        if (controller instanceof HouseholdDialogHandler) {
            ((HouseholdDialogHandler) controller).setOnSuccessCallback(onSuccess);
        }
        
        showModalDialog(sourceNode, root, "Thêm hộ khẩu mới");
    }


    public static void openViewHouseholdDialog(HouseholdController householdController, ResidentService memberService, 
    		Household household, Node sourceNode, Runnable onSuccess) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(Configs.VIEW_HOUSEHOLD_DIALOG_PATH));
        Parent root = loader.load();

        // Get the controller, set the household and callback
        ViewHouseholdDialogHandler controller = loader.getController();
        controller.setHouseholdController(householdController);
        controller.setMemberService(memberService);
        
        if (controller instanceof HouseholdDialogHandler) {
            ((HouseholdDialogHandler) controller).setHousehold(household);
            ((HouseholdDialogHandler) controller).setOnSuccessCallback(onSuccess);
        }
        
        showModalDialog(sourceNode, root, "Chi tiết hộ khẩu");
    }

    /**
     * Loads a new scene and sets it as the current scene.
     *
     * @param event     The event that triggered the navigation
     * @param fxmlPath  The path to the FXML file to load
     * @param title     The title for the stage
     * @throws IOException If the FXML file cannot be loaded
     */
    private static void loadNewScene(ActionEvent event, String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(SceneUtils.class.getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Shows a modal dialog.
     *
     * @param sourceNode The node that triggered the dialog
     * @param root       The root of the dialog content
     * @param title      The title for the dialog
     */
    private static void showModalDialog(Node sourceNode, Parent root, String title) {
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(sourceNode.getScene().getWindow());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Interface for form controllers that need a success callback.
     */
    public interface FormCallback {
        void setOnSuccessCallback(Runnable callback);
    }

    /**
     * Interface for household form controllers.
     */
    public interface HouseholdDialogHandler extends FormCallback {
        void setHousehold(Household household);
    }

    public static void openAddResidentDialog(ResidentController residentController, Button sourceButton, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(Configs.ADD_RESIDENT_DIALOG_PATH));
            AddResidentDialogHandler controller = new AddResidentDialogHandler(residentController);
            loader.setController(controller);
            
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Thêm nhân khẩu mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(sourceButton.getScene().getWindow());
            stage.setResizable(false);
            
            // Set callback to refresh table after successful save
            controller.setOnSuccessCallback(onSuccess);
            
            // Show dialog
            stage.showAndWait();
            
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở form thêm nhân khẩu", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void openViewResidentDialog(ResidentController residentController, Resident resident, Button sourceButton, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(Configs.ADD_RESIDENT_DIALOG_PATH));
            AddResidentDialogHandler controller = new AddResidentDialogHandler(residentController);
            loader.setController(controller);
            
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Thông tin nhân khẩu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(sourceButton.getScene().getWindow());
            stage.setResizable(false);
            
            // Set resident data and callback
            controller.setResident(resident);
            controller.setOnSuccessCallback(onSuccess);
            
            // Show dialog
            stage.showAndWait();
            
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Lỗi", "Không thể mở thông tin nhân khẩu", e.getMessage());
            e.printStackTrace();
        }
    }

}
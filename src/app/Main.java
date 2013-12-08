package app;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import sun.reflect.misc.ReflectUtil;

import java.io.IOException;

public class Main extends Application {

    private final Injector injector = Guice.createInjector(new ApplicationModule());

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/mainView.fxml"));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> aClass) {
                    return injector.getInstance(aClass);
                }
            });
            Parent root = (Parent)fxmlLoader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

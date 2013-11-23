package app;

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

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/mainView.fxml"));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> aClass) {
                    try {
                        System.out.println("Main loader called! aClass = " + aClass);
                        return ReflectUtil.newInstance(aClass); // point to inject EventBus instance
                    } catch (InstantiationException e) {
                        throw new RuntimeException();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Parent root = (Parent)fxmlLoader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("ВСЕ МЕСТА БЕЗ РЕГИСТРАЦИИ И СМС ПРЯМО СЕЙЧАС");
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

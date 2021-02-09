package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXPersons;
import com.svoemesto.ivfx.tables.IVFXProjects;
import com.svoemesto.ivfx.utils.ConvertToFxImage;
import com.svoemesto.ivfx.utils.OverlayImage;
import javafx.concurrent.Task;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadListPersonsTask extends Task<List<IVFXPersons>> {
    private IVFXProjects ivfxProject;
    private boolean withPreview;
    private ProgressBar progressBar;

    public LoadListPersonsTask(IVFXProjects ivfxProject, boolean withPreview, ProgressBar progressBar) {
        this.ivfxProject = ivfxProject;
        this.withPreview = withPreview;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXPersons> call() throws Exception {
        List<IVFXPersons> listPersons = new ArrayList<>();

        final int[] iProgress = {0};
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_persons WHERE project_id = " + ivfxProject.getId() + " ORDER BY name";

            String sqlCnt = "SELECT COUNT(*) AS CNT FROM (" + sql + ") AS tmp";
            ResultSet rsCnt = null;
            rsCnt = statement.executeQuery(sqlCnt);
            rsCnt.next();
            int countRows = rsCnt.getInt("CNT");
            rsCnt.close();

            rs = statement.executeQuery(sql);
            if (progressBar != null) progressBar.setVisible(true);
            while (rs.next()) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress[0] / countRows);

                IVFXPersons person = new IVFXPersons();
                person.setId(rs.getInt("id"));
                person.setOrder(rs.getInt("order_person"));
                person.setProjectId(rs.getInt("project_id"));
                person.setName(rs.getString("name"));
                person.setDescription(rs.getString("description"));
                person.setIvfxProject(IVFXProjects.load(person.getProjectId()));
                listPersons.add(person);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close(); // close result set
                if (statement != null) statement.close(); // close statement
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (withPreview) {
            iProgress[0] = 0;
            for (IVFXPersons ivfxPersons : listPersons) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress[0] / listPersons.size());

                String fileName = ivfxPersons.getPersonPicturePreview();
                File file = new File(fileName);
                for (int i = 0; i < 8; i++) {
                    Label label = new Label(ivfxPersons.getName()+i);
                    label.setPrefWidth(135);
                    ivfxPersons.setLabel(label, i);
                }

                if (!file.exists()) {
                    fileName = ivfxPersons.getPersonPicturePreviewStub();
                    file = new File(fileName);
                }
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    for (int i = 0; i < 8; i++) {
                        ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxPersons.getName())));
                        ivfxPersons.setPreview(imageView, i);
                        Label label = ivfxPersons.getLabel(i);
                        label.setGraphic(imageView);
                        label.setContentDisplay(ContentDisplay.TOP);
                        ivfxPersons.setLabel(label, i);
                    }

                } catch (IOException e) {}

            }
        }
        if (progressBar != null) progressBar.setVisible(false);
        return listPersons;
    }
}

package com.svoemesto.ivfx.tasks;

import com.svoemesto.ivfx.Main;
import com.svoemesto.ivfx.tables.IVFXFiles;
import com.svoemesto.ivfx.tables.IVFXShots;
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

public class LoadListShotsTask extends Task<List<IVFXShots>> {

    private IVFXFiles ivfxFiles;
    private boolean withPreview;
    private ProgressBar progressBar;

    public LoadListShotsTask(IVFXFiles ivfxFiles, boolean withPreview, ProgressBar progressBar) {
        this.ivfxFiles = ivfxFiles;
        this.withPreview = withPreview;
        this.progressBar = progressBar;
    }

    @Override
    protected List<IVFXShots> call() throws Exception {
        List<IVFXShots> listShots = new ArrayList<>();

        final int[] iProgress = {0};
        Statement statement = null;
        ResultSet rs = null;
        String sql;

        try {
            statement = Main.mainConnection.createStatement();

            sql = "SELECT * FROM tbl_shots WHERE file_id = " + ivfxFiles.getId() + " ORDER BY firstFrameNumber";

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

                IVFXShots shot = new IVFXShots();
                shot.setId(rs.getInt("id"));
                shot.setFileId(rs.getInt("file_id"));
                shot.setFirstFrameNumber(rs.getInt("firstFrameNumber"));
                shot.setLastFrameNumber(rs.getInt("lastFrameNumber"));
                shot.setNearestIFrame(rs.getInt("nearestIFrame"));
                shot.setIvfxFile(ivfxFiles);

                listShots.add(shot);

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
            for (IVFXShots ivfxShots : listShots) {

                if (progressBar != null) progressBar.setProgress((double)++iProgress[0] / listShots.size());

                BufferedImage bufferedImage;
                String fileName;
                File file;

                fileName = ivfxShots.getFirstFramePicture();
                file = new File(fileName);
                for (int i = 0; i < 3; i++) {
                    Label label = new Label();
                    label.setMinSize(135, 75);
                    label.setMaxSize(135, 75);
                    label.setPrefSize(135, 75);
                    ivfxShots.setLabelFirst(label, i);
                }

                if (file.exists()) {
                    try {
                        bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 3; i++) {
                            ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxShots.getStart())));
                            ivfxShots.setImageViewFirst(imageView,1);
                            Label label = ivfxShots.getLabelFirst(i);
                            label.setGraphic(imageView);
                            label.setContentDisplay(ContentDisplay.TOP);
                            ivfxShots.setLabelFirst(label, i);
                        }

                    } catch (IOException e) {}

                }

                for (int i = 0; i < 3; i++) {

                    Label label = new Label();
                    label.setMinSize(135, 75);
                    label.setMaxSize(135, 75);
                    label.setPrefSize(135, 75);

                    ivfxShots.setLabelLast(label, i);

                }
                fileName = ivfxShots.getLastFramePicture();
                file = new File(fileName);
                if (file.exists()) {
                    try {
                        bufferedImage = ImageIO.read(file);
                        for (int i = 0; i < 3; i++) {
                            ImageView imageView = new ImageView(ConvertToFxImage.convertToFxImage(OverlayImage.setOverlayUnderlineText(bufferedImage, ivfxShots.getEnd())));
                            ivfxShots.setImageViewLast(imageView,1);
                            Label label = ivfxShots.getLabelLast(i);
                            label.setGraphic(imageView);
                            label.setContentDisplay(ContentDisplay.TOP);
                            ivfxShots.setLabelLast(label, i);
                        }
                    } catch (IOException e) {}

                }
            }
        }

        if (progressBar != null) progressBar.setVisible(false);
        return listShots;

    }
}

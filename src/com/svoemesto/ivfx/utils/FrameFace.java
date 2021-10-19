package com.svoemesto.ivfx.utils;

public class FrameFace {

    public int faceid=0;
    public int projectid=0;
    public int fileid=0;
    public String image_file;
    public int face_id;
    public int person_id;
    public int person_id_recognized;
    public double recognize_probability;
    public int frame_id = 0;
    public String face_file;
    public int start_x;
    public int start_y;
    public int end_x;
    public int end_y;
    public double[] vector;

    public int getFrameNumber() {
       int result = 0;
       String frameNum = this.image_file.substring((this.image_file.length()-10),(this.image_file.length()-4));
        result = Integer.parseInt(frameNum);
       return result;
    }


}

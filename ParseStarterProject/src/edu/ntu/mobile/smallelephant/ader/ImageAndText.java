package edu.ntu.mobile.smallelephant.ader;

public class ImageAndText {
    private String imageUrl;
    private String text;
    private Boolean online;

    public ImageAndText(String imageUrl, String text, Boolean online) {
        this.imageUrl = imageUrl;
        this.text = text;
        this.online = online;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getText() {
        return text;
    }
    public Boolean isOnline(){
    	return online;
    }
}
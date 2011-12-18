package edu.ntu.mobile.smallelephant.ader;

public class ImageAndText {
    private String imageUrl;
    private String text;

    public ImageAndText(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getText() {
        return text;
    }
}
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
    @Override
    public boolean equals(Object object){
    	if( this == object)
    		return true;
    	if( object == null || object.getClass() != this.getClass())
    		return false;
    	ImageAndText obj = (ImageAndText)object;
    	return ( text!=null&&text.equals(obj.text) ) || text==obj.text ;    	
    }
    @Override
    public int hashCode(){
    	int hash = 7;
    	hash = 47*hash + (text == null ? 0 : text.hashCode());
    	return hash;
    }
}
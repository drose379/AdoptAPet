package dylan.com.adoptapet;

import android.graphics.drawable.Drawable;

/**
 * Created by dylan on 11/20/15.
 */
public class MenuItem {

    /**
     * POJO to store nav menu items
     */

    /**
     * 1 = regular
     * 2 = featured
     */
    private int type;

    private String imageUrl;
    private String name;

    private String label;
    private Drawable icon;

    public MenuItem setType( int type ) {
        this.type = type;
        return this;
    }

    public MenuItem setLabel( String label ) {
        this.label = label;
        return this;
    }

    public MenuItem setIcon( Drawable icon ) {
        this.icon = icon;
        return this;
    }

    public MenuItem setPhoto( String url ) {
        imageUrl = url;
        return this;
    }

    public MenuItem setName( String name ) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }
    public Drawable getIcon() {
        return icon;
    }
    public int getType() {return type;}
    public String getImageUrl() {return imageUrl;}
    public String getName() {return name;}

}

package com.markd.applications.android.home.data_objects;

/**
 * Created by Josh on 3/25/2017.
 */

public class MenuItem {
    public String menuIcon;
    public String menuTitle;

    public MenuItem(String menuIcon, String menuTitle) {
        this.menuIcon = menuIcon;
        this.menuTitle = menuTitle;
    }

    public String getMenuIcon() {
        return menuIcon;
    }
    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuTitle() {
        return menuTitle;
    }
    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }
}

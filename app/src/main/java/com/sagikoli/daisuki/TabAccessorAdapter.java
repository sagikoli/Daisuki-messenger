package com.sagikoli.daisuki;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAccessorAdapter extends FragmentPagerAdapter {

    public TabAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: chats chatsfrag=new chats();return chatsfrag;
            case 1: groups groupsfrag=new groups();return groupsfrag;
            case 2: contacts contactsfrag=new contacts();return contactsfrag;
         default:   return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "chats";
            case 1: return "groups";
            case 2: return "contacts";
            default: return "";
        }
    }
}

package com.vine.projectdemo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    // public class ViewPagerAdapter extends FragmentStatePagerAdapter
/*
 【FragmentPagerAdapter】
FragmentPagerAdapter 繼承自 PagerAdapter。相比通用的 PagerAdapter，該類更專注於每一頁均為 Fragment 的情況。
如文檔所述，該類內的每一個生成的 Fragment 都將保存在記憶體之中，因此適用於那些相對靜態的頁，數量也比較少的那種；
如果需要處理有很多頁，並且資料動態性較大、佔用記憶體較多的情況，應該使用FragmentStatePagerAdapter。FragmentPagerAdapter
重載實現了幾個必須的函數，因此來自 PagerAdapter 的函數，我們只需要實現 getCount()，即可。且，由於 FragmentPagerAdapter.instantiateItem()
的實現中，調用了一個新增的虛函數 getItem()，因此，我們還至少需要實現一個 getItem()。因此，總體上來說，相對于繼承自 PagerAdapter，更方便一些。
【FragmentStatePagerAdapter】
FragmentStatePagerAdapter 和前面的 FragmentPagerAdapter 一樣，是繼承子 PagerAdapter。但是，和 FragmentPagerAdapter 不一樣的是
，正如其類名中的 'State' 所表明的含義一樣，該 PagerAdapter 的實現將只保留當前頁面，當頁面離開視線後，就會被消除，釋放其資源；
而在頁面需要顯示時，生成新的頁面(就像 ListView 的實現一樣)。這麼實現的好處就是當擁有大量的頁面時，不必在記憶體中佔用大量的記憶體。
*/
    private static int TAB_COUNT = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return HomeFragment.TITLE;
            case 1:
                return MapFragment.TITLE;
        }
        return super.getPageTitle(position);
    }

//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        registeredFragments.put(position, fragment);
//        return fragment;
//    }

//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return  HomeFragment.newInstance();
//            case 1:
//                return MapFragment.newInstance();
//        }
//        return null;
//    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {

            fragment = new HomeFragment();

        } else if (position == 1) {

            fragment = new MapFragment();

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}

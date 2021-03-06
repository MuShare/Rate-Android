package org.mushare.rate.tab.rate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.mushare.rate.MyFragment;
import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.CurrencyShowList;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.MyCurrencyRate;
import org.mushare.rate.data.RateList;
import org.mushare.rate.url.HttpHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

//import android.text.format.DateFormat;

/**
 * Created by dklap on 12/16/2016.
 */

public class RateFragment extends MyFragment implements RateRecyclerViewAdapter.Callback {
    final static int ADD_CURRENCY_REQUEST = 0;
    final static int CHANGE_BASE_CURRENCY_REQUEST = 1;

    List<MyCurrencyRate> dataSet = new LinkedList<>();

    View viewBaseCurrency;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText editText;
    TextView textViewBaseCurrencyName, textViewBaseCurrencyInfo;
    ImageView imageViewBaseCountryFlag;
    RateRecyclerViewAdapter adapter;
    RateRecyclerView recyclerView;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    ItemTouchHelper itemTouchHelper;
    FloatingActionButton fabAdd;

    AlertDialog dialogAbout;

    private boolean refreshing = true;
    private boolean appBarExpanded = true;
    private boolean showAddButton = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (savedInstanceState != null) {
            refreshing = savedInstanceState.getBoolean("refreshing", true);
            appBarExpanded = savedInstanceState.getBoolean("appbar_expanded", true);
            if (savedInstanceState.getBoolean("show_about_dialog")) showAboutDialog();
        }

        View view = inflater.inflate(R.layout.fragment_rate, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        appBarLayout.setExpanded(appBarExpanded, false);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                appBarExpanded = (verticalOffset == 0);
            }
        });

        recyclerView = (RateRecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        adapter = new RateRecyclerViewAdapter(dataSet, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
                | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return .3f;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    final int position = viewHolder.getAdapterPosition();
                    final String cid = adapter.onItemDismiss(position);
                    checkAddCurrencyButtonVisibility();
//                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, R.string
//                                    .remove_currency_snackbar_message,
//                            BaseTransientBottomBar.LENGTH_LONG).setAction(R.string
//                            .remove_currency_snackbar_action, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            adapter.onItemInsert(position, cid);
//                        }
//                    }).setActionTextColor(Color.WHITE);
//                    View view = snackbar.getView();
//                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view
//                            .getLayoutParams();
//                    params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen
//                            .bottom_navigation_bar_height));
//                    snackbar.show();
                } else showTimeLine(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX > 0) {
                        viewHolder.itemView.findViewById(R.id.timeline_background).setVisibility
                                (View.INVISIBLE);
                        viewHolder.itemView.findViewById(R.id.delete_background).setVisibility(View
                                .VISIBLE);
                    } else if (dX < 0) {
                        viewHolder.itemView.findViewById(R.id.timeline_background).setVisibility
                                (View.VISIBLE);
                        viewHolder.itemView.findViewById(R.id.delete_background).setVisibility(View
                                .INVISIBLE);
                    }
                    viewHolder.itemView.findViewById(R.id.foreground).setTranslationX(dX);
                } else super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder.itemView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
                    viewHolder.itemView.findViewById(R.id.foreground).setBackgroundColor(Color
                            .TRANSPARENT);
//                    viewHolder.itemView.setBackground(getResources().getDrawable(R.drawable
//                            .float_item_background));
                    ViewCompat.animate(viewHolder.itemView).scaleX(1.1f).scaleY(1.1f)
                            .translationZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    4, getResources().getDisplayMetrics())).setDuration(300)
                            .setInterpolator(new OvershootInterpolator()).withLayer();
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
                View foreground = viewHolder.itemView.findViewById(R.id.foreground);
                foreground.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                foreground.setTranslationX(0);
//                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                ViewCompat.animate(viewHolder.itemView).scaleX(1f).scaleY(1f).translationZ(0)
                        .setDuration(300).setInterpolator(new OvershootInterpolator()).withLayer();
                viewHolder.itemView.findViewById(R.id.timeline_background).setVisibility(View.GONE);
                viewHolder.itemView.findViewById(R.id.delete_background).setVisibility(View.GONE);
            }
        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        textViewBaseCurrencyName = (TextView) view.findViewById(R.id.textViewBaseCurrencyCode);
        textViewBaseCurrencyInfo = (TextView) view.findViewById(R.id.textViewBaseCurrencyName);
        imageViewBaseCountryFlag = (ImageView) view.findViewById(R.id.imageViewBaseCountryFlag);

        editText = (EditText) view.findViewById(R.id.editTextBase);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double base = 100;
                String text = s.toString();
                try {
                    base = Double.parseDouble(text.replace(",", ""));
                    if (!text.contains(".")) {
                        String formatted = String.format(Locale.getDefault(), "%1$,d", (long)
                                base);
                        if (!formatted.equals(s.toString())) {
//                            editText.removeTextChangedListener(this);
                            editText.setText(formatted);
                            base = Double.parseDouble(formatted);
//                            editText.addTextChangedListener(this);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } finally {
                    adapter.setBase(base);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        view.findViewById(R.id.currencyBase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangeBaseCurrencyActivity.class);
                startActivityForResult(intent, CHANGE_BASE_CURRENCY_REQUEST);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorBackground);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        fabAdd = (FloatingActionButton) view.findViewById(R.id.actionAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddCurrencyActivity.class);
                startActivityForResult(intent, ADD_CURRENCY_REQUEST);
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (showAddButton && dy > 0 && fabAdd.isShown())
                    fabAdd.hide();
                else if (showAddButton && dy < 0 && !fabAdd.isShown())
                    fabAdd.show();
            }
        });

        toolbar.inflateMenu(R.menu.rate_fragment_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_refresh && !refreshing) {
                    refresh();
                } else if (item.getItemId() == R.id.action_about) {
                    showAboutDialog();
                }
                return false;
            }
        });

        viewBaseCurrency = view.findViewById(R.id.baseCurrency);

        checkAddCurrencyButtonVisibility();
        setBaseCurrency();
        CurrencyShowList.getExchangeCurrencyRateList(dataSet);
        adapter.notifyDataSetChanged();

        if (RateList.getUpdateTime() != 0)
            toolbar.setSubtitle(getResources().getString(R.string.update_time_prefix) +
                    DateFormat.getDateTimeInstance().format(new Date(RateList.getUpdateTime())));

        return view;
    }

    void showAboutDialog() {
        if (dialogAbout != null) dialogAbout.show();
        else {
            dialogAbout = new AlertDialog.Builder(getContext()).setView(R.layout.dialog_about)
                    .setPositiveButton(getString(R.string.feedback_yes), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String appPackageName = RateFragment.this.getContext()
                                    .getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                        ("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                        ("https://play.google.com/store/apps/details?id=" +
                                                appPackageName)));
                            }
                        }
                    }).setNegativeButton(getString(R.string.feedback_no), null).show();
        }
    }

    void refresh() {
        refreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setTouchEnabled(false);
        Thread thread = new Thread() {
            @Override
            public void run() {
                String lan = Locale.getDefault().getLanguage() + "-" + Locale.getDefault()
                        .getCountry();
                if (HttpHelper.getCurrencyList(lan, CurrencyList.getRevision(lan)) == 200 &&
                        HttpHelper.getCurrencyRates("ff808181568824b701568825c7680000") == 200) {
                    CurrencyShowList.getExchangeCurrencyRateList(dataSet);
                    EventBus.getDefault().post(new RefreshFinishEvent());
                } else EventBus.getDefault().post(new RefreshFailEvent());
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    void setBaseCurrency() {
        MyCurrency currency = CurrencyShowList.getBaseCurrency();
        if (currency == null) return;
        textViewBaseCurrencyName.setText(currency.getCode());
        textViewBaseCurrencyInfo.setText(currency.getName());
        editText.setEnabled(true);
        int resID = getResources().getIdentifier("ic_flag_" + currency.getIcon(),
                "drawable", getContext().getPackageName());
        if (resID != 0) {
            imageViewBaseCountryFlag.setImageResource(resID);
        }
    }

    void startBaseCurrencyViewAnimation() {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8, getResources()
                .getDisplayMetrics());
        viewBaseCurrency.animate().translationY(px).setDuration(60).setInterpolator(new
                DecelerateInterpolator()).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                viewBaseCurrency.animate().translationY(0).setDuration(300).setInterpolator(new
                        OvershootInterpolator()).withLayer();
            }
        });
    }

    void checkAddCurrencyButtonVisibility() {
        showAddButton = CurrencyShowList.getInvisibleCurrencyNumber() > 0;
        if (showAddButton) fabAdd.show();
        else fabAdd.hide();
    }

    public void showTimeLine(int index) {
        RateHistoryFragment fragment = new RateHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cid1", CurrencyShowList.getBaseCurrencyCid());
        bundle.putString("cid2", CurrencyShowList.getExchangeCurrencyCid(index));
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.main_container, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_CURRENCY_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                CurrencyShowList.addExchangeCurrencies(data.getStringArrayListExtra("currency"));
                CurrencyShowList.getExchangeCurrencyRateList(dataSet);
                adapter.notifyDataSetChanged();
                checkAddCurrencyButtonVisibility();
            }
        } else if (requestCode == CHANGE_BASE_CURRENCY_REQUEST) {
            if (resultCode == RESULT_OK) {
                CurrencyShowList.setBaseCurrencyCid(data.getStringExtra("base_currency"));
                CurrencyShowList.getExchangeCurrencyRateList(dataSet);
                setBaseCurrency();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        InputMethodManager keyboard = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (refreshing) {
            refresh();
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("refreshing", refreshing);
        outState.putBoolean("appbar_expanded", appBarExpanded);
        outState.putBoolean("show_about_dialog", dialogAbout != null && dialogAbout.isShowing());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFinishEvent event) {
        setBaseCurrency();
        adapter.notifyDataSetChanged();
        toolbar.setSubtitle(getResources().getString(R.string.update_time_prefix) + DateFormat
                .getDateTimeInstance().format(new Date(RateList.getUpdateTime())));
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setTouchEnabled(true);
        refreshing = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFailEvent event) {
        Toast.makeText(getContext(), R.string.error_refresh_fail, Toast
                .LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setTouchEnabled(true);
        refreshing = false;
    }

    @Override
    public void onBaseCurrencySwapped() {
        CurrencyShowList.getExchangeCurrencyRateList(dataSet);
        setBaseCurrency();
        startBaseCurrencyViewAnimation();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStartDrag(RateRecyclerViewAdapter.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onFragmentRecalled() {
        appBarLayout.setExpanded(true, true);
        recyclerView.smoothScrollToPosition(0);
    }

    private static class RefreshFinishEvent {
    }

    private static class RefreshFailEvent {
    }
}


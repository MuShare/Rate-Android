package org.mushare.rate.fragment.rate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyRate;
import org.mushare.rate.data.MyCurrency;

import java.util.List;
import java.util.Locale;

/**
 * Created by dklap on 12/31/2016.
 */
class RateRecyclerViewAdapter extends RecyclerView.Adapter<RateRecyclerViewAdapter.ViewHolder> {
    private List<CurrencyRate> mDataset;
    private double base = 1d;

    // Provide a suitable constructor (depends on the kind of dataset)
    RateRecyclerViewAdapter(List<CurrencyRate> myDataset) {
        mDataset = myDataset;
    }

    void setBase(double base) {
        this.base = base;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RateRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exchange_currency, parent, false);
//        TextView v = new TextView(parent.getContext());
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CurrencyRate currencyRate = mDataset.get(position);
        MyCurrency myCurrency = currencyRate.getCurrency();
//        Currency currency = Currency.getInstance(myCurrency.getCode());
        holder.textViewExchangeRate.setText(String.format(Locale.getDefault(), "%1$,.2f", base *
                currencyRate.getRate()));
        holder.textViewCurrencyCode.setText(myCurrency.getCode());
        holder.textViewCurrencyName.setText(myCurrency.getName());
        int resID = holder.getContext().getResources().getIdentifier("ic_flag_" + myCurrency.getIcon(), "drawable", holder.getContext().getPackageName());
        if (resID != 0) {
            holder.imageViewCountryFlag.setImageResource(resID);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textViewExchangeRate;
        TextView textViewCurrencyCode;
        TextView textViewCurrencyName;
        ImageView imageViewCountryFlag;

        ViewHolder(View v) {
            super(v);
            textViewExchangeRate = (TextView) v.findViewById(R.id.textViewExchangeRate);
            textViewCurrencyCode = (TextView) v.findViewById(R.id.textViewCurrencyCode);
            textViewCurrencyName = (TextView) v.findViewById(R.id.textViewCurrencyName);
            imageViewCountryFlag = (ImageView) v.findViewById(R.id.imageViewCountryFlag);
        }

        Context getContext() {
            return itemView.getContext();
        }
    }
}
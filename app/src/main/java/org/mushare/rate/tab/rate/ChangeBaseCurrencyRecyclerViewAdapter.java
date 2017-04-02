package org.mushare.rate.tab.rate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mushare.rate.R;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.MyCurrencyWrapper;

import java.util.List;

/**
 * Created by dklap on 3/9/2017.
 */

class ChangeBaseCurrencyRecyclerViewAdapter extends RecyclerView
        .Adapter<ChangeBaseCurrencyRecyclerViewAdapter.ViewHolder> {
    private List<MyCurrencyWrapper> mDataset;
    private Callback mCallback;

    ChangeBaseCurrencyRecyclerViewAdapter(List<MyCurrencyWrapper> dataset, Callback
            callback) {
        mDataset = dataset;
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_base_currency_change, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyCurrencyWrapper myCurrencyWrapper = mDataset.get(position);
//        Currency currency = Currency.getInstance(myCurrency.getCode());
        holder.textViewAlphabet.setText(String.valueOf(myCurrencyWrapper.getCharacter()));
        MyCurrency myCurrency = myCurrencyWrapper.getCurrency();
        holder.textViewCurrencyCode.setText(myCurrency.getCode());
        holder.textViewCurrencyName.setText(myCurrency.getName());
        int resID = holder.imageViewCountryFlag.getContext().getResources().getIdentifier
                ("ic_flag_" + myCurrency.getIcon(), "drawable", holder.imageViewCountryFlag
                        .getContext().getPackageName());
        if (resID != 0) {
            holder.imageViewCountryFlag.setImageResource(resID);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onBaseCurrencySelected(myCurrencyWrapper.getCid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    interface Callback {
        void onBaseCurrencySelected(String cid);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textViewAlphabet;
        TextView textViewCurrencyCode;
        TextView textViewCurrencyName;
        ImageView imageViewCountryFlag;

        ViewHolder(View v) {
            super(v);
            textViewAlphabet = (TextView) v.findViewById(R.id.textViewAlphabet);
            textViewCurrencyCode = (TextView) v.findViewById(R.id.textViewCurrencyCode);
            textViewCurrencyName = (TextView) v.findViewById(R.id.textViewCurrencyName);
            imageViewCountryFlag = (ImageView) v.findViewById(R.id.imageViewCountryFlag);
        }

    }
}

package org.mushare.rate.tab.rate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.mushare.rate.R;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.MyCurrencyWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dklap on 3/9/2017.
 */

public class AddCurrencyRecyclerViewAdapter extends RecyclerView
        .Adapter<AddCurrencyRecyclerViewAdapter.ViewHolder> {
    private List<MyCurrencyWrapper> mDataset;
    private ArrayList<String> mSelected = new ArrayList<>();

    public AddCurrencyRecyclerViewAdapter(List<MyCurrencyWrapper> dataset) {
        this.mDataset = dataset;
    }

    public ArrayList<String> getSelected() {
        return mSelected;
    }

    public void setSelected(ArrayList<String> selected) {
        mSelected = selected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_currency_add, parent, false));
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
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(mSelected.contains(myCurrencyWrapper.getCid()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mSelected.add(myCurrencyWrapper.getCid());
                else mSelected.remove(myCurrencyWrapper.getCid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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
        CheckBox checkBox;

        ViewHolder(View v) {
            super(v);
            textViewAlphabet = (TextView) v.findViewById(R.id.textViewAlphabet);
            textViewCurrencyCode = (TextView) v.findViewById(R.id.textViewCurrencyCode);
            textViewCurrencyName = (TextView) v.findViewById(R.id.textViewCurrencyName);
            imageViewCountryFlag = (ImageView) v.findViewById(R.id.imageViewCountryFlag);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.toggle();
                }
            });
        }

    }
}

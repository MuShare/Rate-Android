package org.mushare.rate.tab.rate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.mushare.rate.R;
import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.CurrencyShowList;
import org.mushare.rate.data.MyCurrency;
import org.mushare.rate.data.MyCurrencyRate;
import org.mushare.rate.data.RateList;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by dklap on 12/31/2016.
 */
class RateRecyclerViewAdapter extends RecyclerView.Adapter<RateRecyclerViewAdapter.ViewHolder> {
    private List<MyCurrencyRate> mDataset;
    private Callback mCallback;
    private double base = 100;

    // Provide a suitable constructor (depends on the kind of dataset)
    RateRecyclerViewAdapter(List<MyCurrencyRate> myDataset, Callback myCallback) {
        mDataset = myDataset;
        mCallback = myCallback;
    }

    public String onItemDismiss(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        return CurrencyShowList.removeExchangeCurrencyCid(position);
    }

    public void onItemInsert(int position, String cid) {
        mDataset.add(position, new MyCurrencyRate(CurrencyList.get(cid), RateList.get(cid,
                CurrencyShowList.getBaseCurrencyCid())));
        CurrencyShowList.insertExchangeCurrencyCid(position, cid);
        notifyItemInserted(position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
                CurrencyShowList.swapExchangeCurrencyCid(i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataset, i, i - 1);
                CurrencyShowList.swapExchangeCurrencyCid(i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    void setBase(double base) {
        this.base = base;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RateRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View swipeLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_exchange_currency, parent, false);
        return new ViewHolder(swipeLayout);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        MyCurrencyRate myCurrencyRate = mDataset.get(position);
        Double rate = myCurrencyRate.getRate();
//        Currency currency = Currency.getInstance(myCurrency.getCode());
        if (rate != null)
            holder.textViewExchangeRate.setText(String.format(Locale.getDefault(), "%1$,.2f",
                    base * rate));
        else
            holder.textViewExchangeRate.setText(holder.textViewExchangeRate.getResources()
                    .getString(R.string.unknown));
        MyCurrency myCurrency = myCurrencyRate.getCurrency();
        holder.textViewCurrencyCode.setText(myCurrency.getCode());
        holder.textViewCurrencyName.setText(myCurrency.getName());
        int resID = holder.imageViewCountryFlag.getContext().getResources().getIdentifier
                ("ic_flag_" + myCurrency.getIcon(), "drawable", holder.imageViewCountryFlag
                        .getContext().getPackageName());
        if (resID != 0) {
            holder.imageViewCountryFlag.setImageResource(resID);
        }
        holder.foreground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencyShowList.swapBaseCurrencyCid(holder.getAdapterPosition());
                mCallback.onBaseCurrencyChanged();
            }
        });
        holder.foreground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.onStartDrag(holder);
                return true;
            }
        });
        holder.textViewExchangeRate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String rate = holder.textViewExchangeRate.getText().toString().replace(",", "");
                if (!rate.equals(holder.textViewExchangeRate.getResources().getString(R.string
                        .unknown))) {
                    ClipboardManager clipboard = (ClipboardManager) v.getContext()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(rate, rate);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(v.getContext(), v.getResources().getString(R.string
                            .info_copy_success), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    interface Callback {
        void onBaseCurrencyChanged();

        void onStartDrag(ViewHolder viewHolder);
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
        View foreground;

        ViewHolder(View v) {
            super(v);
            textViewExchangeRate = (TextView) v.findViewById(R.id.checkBox);
            textViewCurrencyCode = (TextView) v.findViewById(R.id.textViewCurrencyCode);
            textViewCurrencyName = (TextView) v.findViewById(R.id.textViewCurrencyName);
            imageViewCountryFlag = (ImageView) v.findViewById(R.id.imageViewCountryFlag);
            foreground = v.findViewById(R.id.foreground);
        }

    }
}
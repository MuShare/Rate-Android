package mushare.org.rate.fragment.rate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mushare.org.rate.R;
import mushare.org.rate.data.Currency;
import mushare.org.rate.data.CurrencyRate;

/**
 * Created by dklap on 12/31/2016.
 */
class RateRecyclerViewAdapter extends RecyclerView.Adapter<RateRecyclerViewAdapter.ViewHolder> {
    private List<CurrencyRate> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textViewExchangeRate;
        TextView textViewCurrencyName;
        TextView textViewCurrencyInfo;

        ViewHolder(View v) {
            super(v);
            textViewExchangeRate = (TextView) v.findViewById(R.id.textViewExchangeRate);
            textViewCurrencyName = (TextView) v.findViewById(R.id.textViewCurrencyName);
            textViewCurrencyInfo = (TextView) v.findViewById(R.id.textViewCurrencyInfo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    RateRecyclerViewAdapter(List<CurrencyRate> myDataset) {
        mDataset = myDataset;
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
        Currency currency = currencyRate.getCurrency();
        holder.textViewExchangeRate.setText(String.valueOf(currencyRate.getRate()));
        holder.textViewCurrencyName.setText(currency.getCode());
        holder.textViewCurrencyInfo.setText(currency.getName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
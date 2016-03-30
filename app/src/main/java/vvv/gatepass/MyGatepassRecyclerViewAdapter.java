package vvv.gatepass;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import vvv.gatepass.GatepassFragment.OnGatepassListFragmentInteractionListener;

public class MyGatepassRecyclerViewAdapter extends RecyclerView.Adapter<MyGatepassRecyclerViewAdapter.ViewHolder> {

    private final List<GatepassListViewItem> mValues;
    private final OnGatepassListFragmentInteractionListener mListener;

    public MyGatepassRecyclerViewAdapter(List<GatepassListViewItem> items, OnGatepassListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gatepass_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.GP.setText(holder.mItem.gp_GatepassNumber);
        holder.setStatus.setText(holder.mItem.gp_RequestStatus);
        holder.inTime.setText(holder.mItem.gp_InTime);
        holder.outTime.setText(holder.mItem.gp_OutTime);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onGatepassListFragmentInteraction(v.getContext(),holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView GP;
        public final TextView inTime;
        public final TextView outTime;
        public final TextView setStatus;
        public GatepassListViewItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            GP = (TextView) view.findViewById(R.id.GP);
            setStatus = (TextView) view.findViewById(R.id.setStatus);
            inTime = (TextView) view.findViewById(R.id.inTime);
            outTime = (TextView) view.findViewById(R.id.outTIme);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + setStatus.getText() + "'";
        }
    }
}

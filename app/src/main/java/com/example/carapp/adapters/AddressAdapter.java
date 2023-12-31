package com.example.carapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.carapp.R;
import com.example.carapp.entities.AddressItem;

import java.util.List;

public class AddressAdapter extends ArrayAdapter<AddressItem> {
    private LayoutInflater inflater;

    public AddressAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    public void setAddressItems(List<AddressItem> addressItems) {
        clear();
        addAll(addressItems);
    }
    public void addAddressItem(AddressItem addressItem) {
        add(addressItem);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_address, parent, false);

            holder = new ViewHolder();
            holder.addressText = convertView.findViewById(R.id.address_text);
            holder.distanceText = convertView.findViewById(R.id.distance_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AddressItem item = getItem(position);
        if (item != null) {
            holder.addressText.setText(item.getAddress());
            holder.distanceText.setText(String.format("%.2f km", item.getDistance()));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView addressText;
        TextView distanceText;
    }
}


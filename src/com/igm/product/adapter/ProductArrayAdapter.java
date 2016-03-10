package com.igm.product.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.igm.product.R;
import com.igm.product.entity.Part;
import com.igm.product.entity.PartList;

import java.util.List;

/**
 * User: Amir Nikjoo,  01/23/2016,  04:03 PM
 */
public class ProductArrayAdapter extends BaseAdapter implements ListAdapter, Filterable {
    private final Context context;
    //    private PartList parts;
    private List<Part> parts;
    private List<Part> allParts;
//    private PartList allParts;

    public ProductArrayAdapter(Context context, List<Part> values) {
        this.context = context;
        this.parts = values;
        this.allParts = values;
    }

    @Override
    public int getCount() {
        return parts.size();
    }

    @Override
    public Object getItem(int i) {
        return parts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return parts.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.product_list_item, null);
        TextView txtDesc = (TextView) v.findViewById(R.id.textDescription);
        txtDesc.setText(parts.get(position).getDescription());

        TextView txtCarType = (TextView) v.findViewById(R.id.txtCarType);
        int i = parts.get(position).getCarType();

        if (i == 1) {
            txtCarType.setText("دوو");
        } else if (i == 2) {
            txtCarType.setText("پژو");
        } else if (i == 3) {
            txtCarType.setText("پراید");
        }

        return v;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                PartList filteredList = new PartList();
                if (charSequence == null || charSequence.length() == 0) {
                    results.count = allParts.size();
                    results.values = allParts;
                } else {
                    for (int i = 0; i < allParts.size(); i++) {
                        Part p = allParts.get(i);

                        String searchPhrase = doReplaceNumbers((String) charSequence);
                        if (p.getDescription().contains(searchPhrase)) {
                            filteredList.add(p);
                        }
                    }
                    results.count = filteredList.size();
                    results.values = filteredList;
                }

//                Toast.makeText(context, "نتیجه :" + results.count + " ردیف ", Toast.LENGTH_SHORT).show();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                parts = (List<Part>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }

    private static int ascii2windows1256(int ascii) {
        int code = ascii;
        switch (ascii) {
            case 1776:       //0
                code = 48;
                break;

            case 1777:       //1
                code = 49;
                break;

            case 1778:      //2
                code = 50;
                break;

            case 1779:        //3
                code = 51;
                break;

            case 1780:        //4
            case 1636:
                code = 52;
                break;

            case 1781:       //5
                code = 53;
                break;

            case 1782:      //6
                code = 54;
                break;

            case 1783:       //7
                code = 55;
                break;

            case 1784:       //8
                code = 56;
                break;

            case 1785:      //9
                code = 57;
                break;

            default:
//                if (ascii > 256)
//                    code = 0x8C;//Iran system '?'
//                else
                code = ascii;
        }
        return code;
    }


    public static String doReplaceNumbers(String str) {
        StringBuilder filtered = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char current = str.charAt(i);
            filtered.append((char) ascii2windows1256(current));
        }

        return filtered.toString();
    }
}

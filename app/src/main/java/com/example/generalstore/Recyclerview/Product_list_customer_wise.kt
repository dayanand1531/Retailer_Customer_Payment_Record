package com.example.generalstore.Recyclerview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.generalstore.Datamodal.PaymentModal
import com.example.generalstore.Datamodal.ProductListModal
import com.example.generalstore.R
import com.example.generalstore.SqliteDatabase.MyCustomerDatahelper

class Product_list_customer_wise(var list: MutableList<PaymentModal>,var context: Context): RecyclerView.Adapter<Product_list_customer_wise.RowData>() {

    class RowData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var  date=itemView.findViewById<TextView>(R.id.date)
      /*  var  product_namme=itemView.findViewById<TextView>(R.id.tv_productname)
        var  price=itemView.findViewById<TextView>(R.id.tv_product_price)
        var  quantity=itemView.findViewById<TextView>(R.id.tv_productQuality)
        var  subtolat=itemView.findViewById<TextView>(R.id.tv_totalPrice)*/
        var  payment_id=itemView.findViewById<TextView>(R.id.order_id)
        var lineralayout=itemView.findViewById<ConstraintLayout>(R.id.liner_layout)
        var expandablelayout=itemView.findViewById<ConstraintLayout>(R.id.expandable_layout)
        var recyclerView=itemView.findViewById<RecyclerView>(R.id.recyclerView)
        var substotal=itemView.findViewById<TextView>(R.id.tv_subtotal)
        var previousbalance=itemView.findViewById<TextView>(R.id.tv_report_privous_balance)
        var distcount=itemView.findViewById<TextView>(R.id.tv_grandtotal)
        var charges=itemView.findViewById<TextView>(R.id.tv_charges_dialog)
        var payamunt=itemView.findViewById<TextView>(R.id.tv_payamount_dailog)
        var paymenttypes=itemView.findViewById<TextView>(R.id.tv_paymenttype_dailog)
        var grantotal=itemView.findViewById<TextView>(R.id.tv_grandtotal_dialog)
        var remain=itemView.findViewById<TextView>(R.id.tv_remain_dialog)
      //  var customerid=itemView.findViewById<TextView>(R.id.tv_total_dailog)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowData {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.row_product_item_report,parent,false)
        return RowData(view)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RowData, position: Int) {
        var data:PaymentModal=list[position]
        holder?.date?.text= data.date
        holder?.substotal?.text=data.sublotal
        holder?.previousbalance?.text=data.privious_payment
        holder?.distcount?.text=data.discount
        holder?.charges?.text=data.charges
        holder?.payment_id?.text=data.paymentid
        holder?.payamunt?.text=data.payamount
        holder?.paymenttypes?.text=data.paymenttypes
        holder?.grantotal?.text=data.grandtoatal
        holder?.remain?.text=data.remain

        var isexpandable:Boolean=list[position].expadable
        holder.expandablelayout.visibility=if (isexpandable) View.VISIBLE else View.GONE
        holder?.lineralayout?.setOnClickListener {
            getproductList(holder.recyclerView,context,data.paymentid)
            var productlist=list[position]
            productlist.expadable=!productlist.expadable
            notifyItemChanged(position)

        }

    }

    override fun getItemCount(): Int {
        return list.size

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getproductList(recyclerView:RecyclerView, context: Context,paymentid:String){
      //  var context=this
        var db= MyCustomerDatahelper(context)
        var getdata=db.getProductlistCustomerWise(paymentid)
        var list=ArrayList<ProductListModal>()
        recyclerView.layoutManager= LinearLayoutManager(context)
        for (i in 0..(getdata.size-1)){
            list.add(ProductListModal(getdata.get(i).productname,getdata.get(i).price,getdata.get(i).quality,getdata.get(i).totalprice))
        }
        var adapter=ProductSaleReport(list)
        recyclerView.adapter=adapter

    }


}
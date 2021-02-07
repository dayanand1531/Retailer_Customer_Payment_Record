package com.example.generalstore

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.generalstore.Datamodal.PaymentModal
import com.example.generalstore.SqliteDatabase.MyCustomerDatahelper
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_payment.tv_grandtotal
import kotlinx.android.synthetic.main.activity_payment.tv_subtotal
import kotlinx.android.synthetic.main.dialoggrandtotal.view.*

class Payment : AppCompatActivity() {
    var discount=0
    var charges=0
    var remain=0
    var totalprice:Float = 0f
    var paymentopt: String = ""
    var paymentotp: Array<String> =
        arrayOf("Cash", "Debit card", "Credit card", "Net Banking", "E-Wallat")
    var calculate:Array<Float> = arrayOf(0f,0f,0f,0f,0f)
    lateinit var customer_id:String
    var context = this
    var db = MyCustomerDatahelper(context)
    var paymentid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        customer_id=intent.getStringExtra("customer_id")
        var total = intent.getStringExtra("grandtotal")
        tv_subtotal.text = total
        totalprice = total.toFloat()
        tv_grandtotal.text = totalprice.toString()
        getPreviouseBalance()
        spinnerList()
      var pre_balance=tv_priveous_balance.text.toString()
        calculate.set(0,totalprice.toFloat())
        calculate.set(1,pre_balance.toFloat())
        tv_grandtotal.text=calculate.sum().toString()

        et_discount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et_discount.text.isEmpty()){
                    calculate.set(2,0f)
                }else{
                   var dis= et_discount.text.toString()
                    calculate.set(2,-dis.toFloat())
                }
                tv_grandtotal.text=calculate.sum().toString()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validationDiscont()
            }

        })

        et_charges.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et_charges.text.isEmpty()){
                    calculate.set(3,0f)
                }else{
                    var dis= et_charges.text.toString()
                    calculate.set(3,dis.toFloat())
                }
                tv_grandtotal.text=calculate.sum().toString()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validationCharges()
            }
        })

        et_payedamount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (et_payedamount.text.isEmpty()){
                    calculate.set(4,0f)
                }else{
                    var dis= et_payedamount.text.toString()
                    calculate.set(4,dis.toFloat())
                    var t=calculate.get(0)+calculate.get(1)+calculate.get(2)+calculate.get(3)
                    var pamount=calculate.get(4)
                    var rmain=t-pamount
                    tv_remain.text=rmain.toString()
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validationPayamount()

            }
        })

        btn_pay.setOnClickListener(View.OnClickListener {
            if (!validationDiscont() || !validationCharges() || !validationPayamount()) {
                return@OnClickListener
            } else {
              //  grandTotal()
                paymentSlip()
            }
        })

        spinner_paymentopt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                paymentopt = paymentotp[position]

            }

        }
    }

    fun paymentSlip() {

        var layout = LayoutInflater.from(this).inflate(R.layout.dialoggrandtotal, null)
        val dailog = AlertDialog.Builder(this)
            .setView(layout)
            .setTitle("Payment Slip")
        var dailogshow = dailog.show()
        layout.tv_charges_dialog.text = et_charges.text.toString()
        layout.tv_subtotal.text = totalprice.toString()
        layout.tv_remain_dialog.text = tv_remain.text.toString()
        layout.tv_grandtotal.text = et_discount.text.toString()
        layout.tv_grandtotal_dialog.text = tv_grandtotal.text.toString()
        layout.tv_payamount_dailog.text = et_payedamount.text.toString()
        layout.tv_paymenttype_dailog.text = paymentopt
        layout.tv_report_privous_balance.text=tv_priveous_balance.text.toString()
        layout.btn_ok.setOnClickListener(View.OnClickListener {
            var payment = PaymentModal(
                layout.tv_subtotal.text.toString(),
                layout.tv_grandtotal.text.toString(),
                layout.tv_charges_dialog.text.toString(),
                layout.tv_payamount_dailog.text.toString(),
                layout.tv_grandtotal_dialog.text.toString(),
                layout.tv_remain_dialog.text.toString(),
                layout.tv_paymenttype_dailog.text.toString(),
                customer_id,
                layout.tv_report_privous_balance.text.toString()
            )
            db.insertPayment(payment)
            dailogshow.dismiss()

            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            var db=MyCustomerDatahelper(this)
            var result=db.getLastPaymentID()
            if (result!=null){
                paymentid=result.paymentid

                Log.d("Paymentid", paymentid)
                db.savePaymentidInProduct(customer_id,paymentid)
            }
        })

    }

    fun validationDiscont(): Boolean {
        var discount = et_discount.text.toString().trim()

        if (discount.isEmpty()) {
            tv_discount_error.visibility = View.VISIBLE
            tv_discount_error.text = "*Enter discount amount."
            return false
        } else {
            tv_discount_error.visibility = View.GONE
            tv_discount_error.text = ""

            return true

        }

    }

    fun validationCharges(): Boolean {
        var charges = et_charges.text.toString().trim()

        if (charges.isEmpty()) {
            tv_charge_error.visibility = View.VISIBLE
            tv_charge_error.text = "*Enter charges amount."
            return false
        } else {
            tv_charge_error.visibility = View.GONE
            tv_charge_error.text = ""

            return true

        }

    }

    fun validationPayamount(): Boolean {
        var payamount = et_payedamount.text.toString().trim()

        if (payamount.isEmpty()) {
            tv_payamount_error.visibility = View.VISIBLE
            tv_payamount_error.text = "*Enter pay amount."
            return false
        } else {
            tv_payamount_error.visibility = View.GONE
            tv_payamount_error.text = ""

            return true

        }

    }

    fun getPreviouseBalance(){

        var db=MyCustomerDatahelper(this)
        var result=db.getPriviousBalnace(customer_id.toInt())
        if (result!=null){
            tv_priveous_balance.text=result.privious_payment

        }
        else{
            tv_priveous_balance.text= 0.toString()

        }
    }

    fun spinnerList(){

        var arrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentotp)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_paymentopt.adapter = arrayAdapter

    }

    /*fun paymentId( paymentid:String){
        var db=MyCustomerDatahelper(this)
        var result=db.getLastPaymentID()
        if (result!=null){
            paymentid=result.paymentid
        }
    }*/

   /* fun getCustomerdetail(){
        customer_id=intent.getStringExtra("customer_id")
        var total = intent.getStringExtra("grandtotal")
        tv_subtotal.text = total
        totalprice = total.toInt()
        tv_grandtotal.text = totalprice.toString()
    }*/

   /* fun grandTotal(){
        subtotal=       tv_subtotal.text.toString().toInt()
        previous_remain=tv_priveous_balance.text.toString().toInt()
        discount=       et_discount.text.toString().toInt()
        charges=        et_charges.text.toString().toInt()
        payamount=      et_payedamount.text.toString().toInt()
        grandtotal=     tv_grandtotal.text.toString().toInt()
        remain=         tv_remain.text.toString().toInt()

        var gtotal=subtotal+previous_remain+charges-discount
        tv_grandtotal.text=gtotal.toString()
        var remain=gtotal-payamount
        tv_remain.text=remain.toString()


    }*/

    /*fun calculate(view: View) {
        if (!validationDiscont() && !validationCharges() && !validationPayamount()) {
            return
        }else{
            grandTotal()
        }

    }*/
}

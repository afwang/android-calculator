package com.example.calculator;

import android.os.Parcel;
import android.os.Parcelable;

public class CalcOperation implements Parcelable{
	public double operand1;
	public double operand2;
	public char operation;
	public double result;
	
	public static final Parcelable.Creator<CalcOperation> CREATOR
		= new Parcelable.Creator<CalcOperation>() {

			@Override
			public CalcOperation createFromParcel(Parcel source) {
				return new CalcOperation(source);
			}

			@Override
			public CalcOperation[] newArray(int size) {
				return new CalcOperation[size];
			}
		};
	
	public CalcOperation(double op1, double op2, char op, double result) {
		operand1 = op1;
		operand2 = op2;
		operation = op;
		this.result = result;
	}
	
	private CalcOperation(Parcel in) {
		operand1 = in.readDouble();
		operand2 = in.readDouble();
		operation = (char)in.readInt();
		result = in.readDouble();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDouble(operand1);
		dest.writeDouble(operand2);
		dest.writeInt(operation);
		dest.writeDouble(result);
	}
}

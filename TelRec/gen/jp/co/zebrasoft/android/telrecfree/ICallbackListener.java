/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: J:\\Workspace\\TelRec\\src\\jp\\co\\zebrasoft\\android\\telrecfree\\ICallbackListener.aidl
 */
package jp.co.zebrasoft.android.telrecfree;
public interface ICallbackListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jp.co.zebrasoft.android.telrecfree.ICallbackListener
{
private static final java.lang.String DESCRIPTOR = "jp.co.zebrasoft.android.telrecfree.ICallbackListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an jp.co.zebrasoft.android.telrecfree.ICallbackListener interface,
 * generating a proxy if needed.
 */
public static jp.co.zebrasoft.android.telrecfree.ICallbackListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jp.co.zebrasoft.android.telrecfree.ICallbackListener))) {
return ((jp.co.zebrasoft.android.telrecfree.ICallbackListener)iin);
}
return new jp.co.zebrasoft.android.telrecfree.ICallbackListener.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_receiveMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.receiveMessage(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jp.co.zebrasoft.android.telrecfree.ICallbackListener
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void receiveMessage(java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_receiveMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_receiveMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void receiveMessage(java.lang.String message) throws android.os.RemoteException;
}

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: J:\\Workspace\\TelRec\\src\\jp\\co\\zebrasoft\\android\\telrecfree\\ICallbackService.aidl
 */
package jp.co.zebrasoft.android.telrecfree;
public interface ICallbackService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jp.co.zebrasoft.android.telrecfree.ICallbackService
{
private static final java.lang.String DESCRIPTOR = "jp.co.zebrasoft.android.telrecfree.ICallbackService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an jp.co.zebrasoft.android.telrecfree.ICallbackService interface,
 * generating a proxy if needed.
 */
public static jp.co.zebrasoft.android.telrecfree.ICallbackService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jp.co.zebrasoft.android.telrecfree.ICallbackService))) {
return ((jp.co.zebrasoft.android.telrecfree.ICallbackService)iin);
}
return new jp.co.zebrasoft.android.telrecfree.ICallbackService.Stub.Proxy(obj);
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
case TRANSACTION_addListener:
{
data.enforceInterface(DESCRIPTOR);
jp.co.zebrasoft.android.telrecfree.ICallbackListener _arg0;
_arg0 = jp.co.zebrasoft.android.telrecfree.ICallbackListener.Stub.asInterface(data.readStrongBinder());
this.addListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeListener:
{
data.enforceInterface(DESCRIPTOR);
jp.co.zebrasoft.android.telrecfree.ICallbackListener _arg0;
_arg0 = jp.co.zebrasoft.android.telrecfree.ICallbackListener.Stub.asInterface(data.readStrongBinder());
this.removeListener(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jp.co.zebrasoft.android.telrecfree.ICallbackService
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
@Override public void addListener(jp.co.zebrasoft.android.telrecfree.ICallbackListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeListener(jp.co.zebrasoft.android.telrecfree.ICallbackListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_addListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void addListener(jp.co.zebrasoft.android.telrecfree.ICallbackListener listener) throws android.os.RemoteException;
public void removeListener(jp.co.zebrasoft.android.telrecfree.ICallbackListener listener) throws android.os.RemoteException;
}

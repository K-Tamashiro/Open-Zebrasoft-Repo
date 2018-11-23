package jp.co.zebrasoft.android.telrecfree;

import jp.co.zebrasoft.android.telrecfree.ICallbackListener;

interface ICallbackService {
	void addListener(ICallbackListener listener);
	void removeListener(ICallbackListener listener);
}
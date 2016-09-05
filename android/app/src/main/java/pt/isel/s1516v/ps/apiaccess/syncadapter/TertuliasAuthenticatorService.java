/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import pt.isel.s1516v.ps.apiaccess.helpers.Util;

public class TertuliasAuthenticatorService extends Service {
    private TertuliasAuthenticator authenticator;

    public TertuliasAuthenticatorService() {
    }

    @Override
    public void onCreate() {
        Util.logd("AUTHENTICATOR SERVICE: onCreate");
        authenticator = new TertuliasAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Util.logd("AUTHENTICATOR SERVICE: onBind");
        return authenticator.getIBinder();
    }
}

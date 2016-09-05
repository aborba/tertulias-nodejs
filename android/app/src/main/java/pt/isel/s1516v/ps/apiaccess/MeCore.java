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

package pt.isel.s1516v.ps.apiaccess;

import pt.isel.s1516v.ps.apiaccess.support.remote.ApiMeCore;

public class MeCore {

    public final String alias;
    public final String firstName;
    public final String lastName;
    public final String email;
    public final String picture;
    public final String myKey;

    public MeCore(ApiMeCore apiMeCore) {
        if (apiMeCore == null) {
            alias = firstName = lastName = email = picture = myKey = null;
            return;
        }
        alias = apiMeCore.alias;
        firstName = apiMeCore.firstName;
        lastName = apiMeCore.lastName;
        email = apiMeCore.email;
        picture = apiMeCore.picture;
        myKey = apiMeCore.myKey;
    }

    @Override
    public String toString() { return alias; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        MeCore other = (MeCore) obj;
        return obj instanceof MeCore && other.alias == this.alias &&
                other.firstName == this.firstName &&
                other.lastName == this.lastName &&
                other.email == this.email &&
                other.picture == this.picture;
    }
}

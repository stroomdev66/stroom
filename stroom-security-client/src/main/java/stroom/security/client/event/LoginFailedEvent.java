/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.security.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class LoginFailedEvent extends GwtEvent<LoginFailedEvent.LoginFailedHandler> {
    private static Type<LoginFailedHandler> TYPE;
    private final String error;

    private LoginFailedEvent(final String error) {
        this.error = error;
    }

    public static Type<LoginFailedHandler> getType() {
        if (TYPE == null) {
            TYPE = new GwtEvent.Type<>();
        }
        return TYPE;
    }

    public static void fire(final HasHandlers handlers, final String error) {
        handlers.fireEvent(new LoginFailedEvent(error));
    }

    @Override
    public Type<LoginFailedHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(final LoginFailedHandler handler) {
        handler.onLoginFailed(this);
    }

    public String getError() {
        return error;
    }

    public interface LoginFailedHandler extends EventHandler {
        void onLoginFailed(LoginFailedEvent event);
    }
}

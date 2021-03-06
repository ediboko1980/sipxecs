/**
 *
 * Copyright (c) 2013 Karel Electronics Corp. All rights reserved.
 * Contributed to SIPfoundry under a Contributor Agreement
 *
 * This software is free software; you can redistribute it and/or modify it under
 * the terms of the Affero General Public License (AGPL) as published by the
 * Free Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 */

package org.sipfoundry.sipxconfig.site.admin.systemaudit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.sipfoundry.sipxconfig.common.CoreContext;
import org.sipfoundry.sipxconfig.common.User;
import org.sipfoundry.sipxconfig.security.UserDetailsImpl;
import org.sipfoundry.sipxconfig.systemaudit.ConfigChange;
import org.sipfoundry.sipxconfig.systemaudit.ConfigChangeContext;
import org.sipfoundry.sipxconfig.systemaudit.ConfigChangeType;
import org.sipfoundry.sipxconfig.systemaudit.ConfigChangeValue;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ComponentClass(allowBody = false, allowInformalParameters = false)
public abstract class ConfigChangeTable extends BaseComponent implements PageBeginRenderListener {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    private static final String FEATURE_PREFIX = "feature.";

    public abstract ConfigChange getConfigChange();

    @Parameter(required = true)
    public abstract ConfigChangeTableModel getSource();

    @Asset("/images/user.png")
    public abstract IAsset getNormalUserIcon();

    @Asset("/images/adminUser.png")
    public abstract IAsset getAdminUserIcon();

    @Asset("/images/phantom.png")
    public abstract IAsset getPhantomUserIcon();

    @InjectObject("spring:configChangeContext")
    public abstract ConfigChangeContext getConfigChangeContext();

    @InjectObject("spring:standardUserDetailsService")
    public abstract UserDetailsService getUserDetailsService();

    @InjectObject("spring:coreContext")
    public abstract CoreContext getCoreContext();

    @Parameter
    public abstract Integer getGroupId();

    @InjectPage(value = ViewConfigChange.PAGE)
    public abstract ViewConfigChange getViewConfigChangePage();

    @Parameter
    public abstract IActionListener getUserListener();

    public abstract int getCurrentRowId();

    public void pageBeginRender(PageEvent event_) {
    }

    public IPage viewConfigChange(List<ConfigChangeValue> configChangeValues, String configChangeType,
            String configChangeAction, String details, Date dateTime, String userName, String ipAddress) {
        ViewConfigChange page = getViewConfigChangePage();
        page.setValues(configChangeValues);
        page.setConfigChangeType(configChangeType);
        page.setConfigChangeAction(configChangeAction);
        page.setDetails(details);
        page.setDateTime(dateTime);
        page.setUserName(userName);
        page.setIpAddress(ipAddress);

        return page;
    }

    public ConfigChangeSqueezer getConverter() {
        return new ConfigChangeSqueezer((getSource().getPage()));
    }

    public String getConfigChangeType() {
        return getMessages().getMessage(getConfigChange().getConfigChangeType());
    }

    public String getAction() {
        return getMessages().getMessage(getConfigChange().getAction());
    }

    public IAsset getUserIcon() {
        User user = getUser();
        if (user != null) {
            if (user.isPhantom()) {
                return getPhantomUserIcon();
            }
            return user.isAdmin() ? getAdminUserIcon() : getNormalUserIcon();
        }
        return null;
    }

    public String getUserIconTitle() {
        User user = getUser();
        if (user != null) {
            if (user.isPhantom()) {
                return getMessages().getMessage("phantomUser");
            }
            String key = user.isAdmin() ? "adminUser" : "normalUser";
            return getMessages().getMessage(key);
        }
        return null;
    }

    public User getUser() {
        String userName = getConfigChange().getUserName();
        if (userName != null) {
            try {
                UserDetailsImpl userDetails = (UserDetailsImpl) getUserDetailsService()
                        .loadUserByUsername(userName);
                User user = getCoreContext().loadUserByUserName(
                        userDetails.getCanonicalUserName());
                return user;
            } catch (UsernameNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    public DateFormat getDateFormat() {
        return DATE_FORMAT;
    }

    public String getDetails() {
        String configChangeType = getConfigChange().getConfigChangeType();
        if (ConfigChangeType.hasTranslatedDetails(configChangeType)) {
            if (configChangeType.equals(ConfigChangeType.FEATURE.getName())) {
                return getMessages().getMessage(
                        FEATURE_PREFIX + getConfigChange().getDetails());
            } else if (configChangeType.equals(ConfigChangeType.SERVER
                    .getName())) {
                return getMessages().getMessage(
                        "label." + getConfigChange().getDetails());
            }
            return getMessages().getMessage(getConfigChange().getDetails());
        }
        return getConfigChange().getDetails();
    }

}

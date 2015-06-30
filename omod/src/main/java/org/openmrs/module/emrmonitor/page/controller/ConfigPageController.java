package org.openmrs.module.emrmonitor.page.controller;

import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.ui.framework.page.PageModel;


public class ConfigPageController {

    public void controller(PageModel model,
                           UiSessionContext sessionContext) {

        sessionContext.requireAuthentication();

    }
}

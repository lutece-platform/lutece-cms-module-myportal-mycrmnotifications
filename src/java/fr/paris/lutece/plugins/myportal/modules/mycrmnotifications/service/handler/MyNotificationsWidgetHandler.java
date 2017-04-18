/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.myportal.modules.mycrmnotifications.service.handler;

import fr.paris.lutece.plugins.crm.business.demand.Demand;
import fr.paris.lutece.plugins.crm.business.demand.DemandFilter;
import fr.paris.lutece.plugins.crm.business.demand.DemandType;
import fr.paris.lutece.plugins.crm.business.user.CRMUser;
import fr.paris.lutece.plugins.crm.service.demand.DemandService;
import fr.paris.lutece.plugins.crm.service.demand.DemandTypeService;
import fr.paris.lutece.plugins.crm.service.user.CRMUserService;
import fr.paris.lutece.plugins.myportal.business.Widget;
import fr.paris.lutece.plugins.myportal.service.handler.WidgetHandler;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



/**
 *
 * MyTasks Widget Handler
 *
 */
public class MyNotificationsWidgetHandler implements WidgetHandler
{
    private static final String NAME = "mynotifications";
    private static final String DESCRIPTION = "MyCRM Notifications Widget";

    // TEMPLATES
    private static final String TEMPLATE_WIDGET_MYNOTIFICATIONS = "skin/plugins/myportal/modules/mycrmnotifications/widget_mynotifications.html";

    // MARKS
    private static final String MARK_MYNOTIFICATION_LIST = "mynotification_list";
    private static final String MARK_ID_WIDGET = "id_widget";

   

    /**
     * {@inheritDoc }
     */
    public String renderWidget( Widget widget, LuteceUser user, HttpServletRequest request )
    {
        DemandFilter filter= new DemandFilter();
        Map<DemandType,Integer> listNotificationNoteRead=new HashMap<DemandType,Integer>() ;

        if( user != null ){
        	
	       CRMUserService crmUserService = CRMUserService.getService(  );
	       CRMUser crmUser = crmUserService.findByUserGuid( user.getName() );
	        
	        if( crmUser != null ){
	   
		        filter.setIdCRMUser(/*crmUser.getIdCRMUser()*/1);
		        List<Demand> listDemand= DemandService.getService().findByFilter(filter);
		        listNotificationNoteRead = getNumberNotifications(listDemand);   
	        }
        }

        Locale locale = request.getLocale(  );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_MYNOTIFICATION_LIST, listNotificationNoteRead );
        model.put( MARK_ID_WIDGET, widget.getIdWidget(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_WIDGET_MYNOTIFICATIONS, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc }
     */
    public String getName(  )
    {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    public String getDescription(  )
    {
        return DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCustomizable(  )
    {
        return true;
    }
    
    /**
     * Get the the notifications not read 
     * @param listDemand the list demand object
     * @return the map of the notification
     */
    private Map<DemandType,Integer> getNumberNotifications( List<Demand> listDemand )
    {
        
		Map<DemandType,Integer> map=new HashMap<DemandType,Integer>() ;
       
        for(DemandType demandType:DemandTypeService.getService().findAll()){
        	for(Demand demand:listDemand){
        		
        		if(demand.getIdDemandType() == demandType.getIdDemandType()){
        			
        			if(map.containsKey(demandType)){
        				
        				map.put(demandType, map.get(demandType.getIdDemandType( ))+demand.getNumberUnreadNotifications());
        			
        			}else{
        				
        				map.put(demandType, demand.getNumberUnreadNotifications());
        			}
        		}
        	}
        	
        }
        

        return map;
    }
}

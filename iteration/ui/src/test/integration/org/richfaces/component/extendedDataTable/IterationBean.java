/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 **/
package org.richfaces.component.extendedDataTable;

import org.richfaces.component.AbstractExtendedDataTable;
import org.richfaces.component.ExtendedDataTableStateLoadedEvent;
import org.richfaces.component.SortOrder;
import org.richfaces.component.UIColumn;
import org.richfaces.model.Filter;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
 */
@SessionScoped
@Named
public class IterationBean implements SystemEventListener, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> values;
    private String widthState = "{'columnsWidthState':{'column1':'210px','column2':'75px'}}";
    private String orderState = "{'columnsOrderState':['column2','column1']}";
    private String sortState = "{'columnsSortState':{'column2':'descending'}}";
    private String filterState = "{'columnsFilterState':{'column2':'6'}}";
    private String[] columnsOrder = null; //{"column1", "column2"};

    private SortOrder sortOrder = SortOrder.ascending;
    private Long filterValue = null;


    public IterationBean() {
        FacesContext.getCurrentInstance().getApplication().subscribeToEvent(ExtendedDataTableStateLoadedEvent.class, this);
        values = new ArrayList<String>(10);
        for (int i = 0; i < 10; i++) {
            values.add(String.valueOf(i));
        }
        sortAscending();
    }

    public List<String> getValues() {
        return values;
    }

    public String getWidthState() {
        return widthState;
    }

    public String getOrderState() {
        return orderState;
    }

    public String getSortState() {
        return sortState;
    }

    public String getFilterState() {
        return filterState;
    }

    public String[] getColumnsOrder() {
        return columnsOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void sort() {
        if (sortOrder == SortOrder.ascending) {
            sortDescending();
        }
        else if (sortOrder == SortOrder.descending) {
            sortAscending();
        }
    }

    private void sortAscending() {
        Collections.sort(values);
        sortOrder = SortOrder.ascending;
    }

    private void sortDescending() {
        Collections.sort(values);
        Collections.reverse(values);
        sortOrder = SortOrder.descending;
    }

    public Filter<?> getFilterImpl() {
        return new Filter<String>() {
            public boolean accept(String value) {
                Long filterValue = getFilterValue();
                if (filterValue == null || filterValue == 0 || filterValue.compareTo(Long.valueOf(value)) >= 0) {
                    return true;
                }
                return false;
            }
        };
    }

    public Long getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Long filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        AbstractExtendedDataTable table = (AbstractExtendedDataTable) event.getSource();
        Iterator<UIComponent> iterator = table.columns();
        while (iterator.hasNext()) {
            UIComponent component = iterator.next();
            if (component instanceof UIColumn) {
                UIColumn column = (UIColumn) component;
                if ("column2".equals(column.getId())) {
                    if (column.getSortOrder() != getSortOrder()) {
                        sort();
                    }
                    Long beanFilterValue = getFilterValue() == null ? 0 : getFilterValue();
                    Long columnFilterValue = column.getFilterValue() == null ? 0 : Long.valueOf(column.getFilterValue().toString());
                    if (! beanFilterValue.equals(columnFilterValue)) {
                        setFilterValue(columnFilterValue);
                        Filter filterImpl = getFilterImpl();
                        Iterator<String> valueIterator = values.iterator();
                        while (valueIterator.hasNext()) {
                            String value = valueIterator.next();
                            if (! filterImpl.accept(value)) {
                                valueIterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return (source instanceof AbstractExtendedDataTable);
    }

}

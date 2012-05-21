package org.richfaces.component.extendedDataTable;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

@RequestScoped
@Named("tableModel")
public class TableModelBean {

    public List<String> getSimple() {
        return Arrays.asList("1", "2");
    }
}

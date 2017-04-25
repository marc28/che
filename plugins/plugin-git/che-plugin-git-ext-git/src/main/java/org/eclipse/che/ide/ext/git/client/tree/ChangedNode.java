package org.eclipse.che.ide.ext.git.client.tree;

public interface ChangedNode {
    boolean isSelected();

    void setSelected(boolean selected);
}

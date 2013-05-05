/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2005 Sascha Hunold <hunoldinho@users.sourceforge.net>
 *
<license>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
</license>
 *
 * $Id: SyncTreeViewer.java,v 1.27 2006/08/09 22:18:39 hunold Exp $
 */

package net.sf.jfilesync.sync2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.gui.viewmodel.TableFileNameRenderer;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.TStyleChooser;
import net.sf.jfilesync.sync2.SyncDiff;
import net.sf.jfilesync.sync2.SyncDiffCollection;
import net.sf.jfilesync.sync2.SyncFile;
import net.sf.jfilesync.sync2.SyncFileSorter;
import net.sf.jfilesync.sync2.SyncTree;
import net.sf.jfilesync.sync2.diffs.SyncDiffTree;
import net.sf.jfilesync.sync2.event.SyncIgnoreFileMessage;
import net.sf.jfilesync.sync2.event.SyncPluginMessage;
import net.sf.jfilesync.sync2.event.SyncShowOnlyFilesMessage;
import net.sf.jfilesync.sync2.event.SyncTreeViewUpdateMessage;
import net.sf.jfilesync.sync2.event.SyncUnhideFileMessage;

public class SyncTreeViewer extends JPanel implements TreeExpansionListener,
        TreeSelectionListener, ActionListener, TEventListener {

    private static final long serialVersionUID = 1L;
    private JTree viewTree;
    private final SyncTree fileTree;
    private final SyncDiffTree diffTree;
    private final SyncDiffCollection diffCol;

    private SyncTreeTable fileTable;
    private SyncTableModel tableModel;
    private DefaultMutableTreeNode root;
    private JScrollPane leftScrollPane, rightScrollPane;
    private JSplitPane splitPane;

    private SyncMouseMotionListener syncMouseMotionListener;

    private final java.util.List<SyncTreeViewListener> syncTreeListeners = new ArrayList<SyncTreeViewListener>();

    protected final static String ACT_TABLE_IGNORE = "tab_ignore";
    protected final static String ACT_TABLE_IGNORE_OTHERS = "tab_ignore_others";
    protected final static String ACT_TABLE_SHOW_ALL = "tab_show_all";
    protected final static String ACT_TREE_IGNORE = "tree_ignore";
    protected final static String ACT_TREE_IGNORE_OTHERS = "tree_ignore_others";
    protected final static String ACT_TREE_SHOW_ALL = "tree_show_all";

    protected final static String ACT_FILE_DIFF = "act_file_diff";

    private final SyncViewPopupMenu tablePopup = new SyncViewPopupMenu(
            SyncViewPopupMenu.TYPE_TABLE);
    private final SyncViewPopupMenu treePopup = new SyncViewPopupMenu(
            SyncViewPopupMenu.TYPE_TREE);

    // options of the diff tree
    private boolean isDiffTree = false;
    private boolean showExcluded = true;

    private final static Logger LOGGER = Logger.getLogger(SyncTreeViewer.class
            .getName());

    public SyncTreeViewer(final SyncTree fileTree, final SyncDiffTree diffTree) {
        this.fileTree = fileTree;
        this.diffTree = diffTree;
        diffCol = diffTree.getSyncDiffCollection();

        TEventMulticaster.getInstance().addTEventListener(this,
                TMessage.ID.SYNC_TREE_VIEW_UPDATE_MESSAGE);

        initUI();
    }

    private void initUI() {

        tableModel = new SyncTableModel(fileTree.getRoot().getChildren());
        fileTable = new SyncTreeTable(tableModel, new TableFileNameRenderer(
                new SyncFileNameRenderer(diffTree)));
        fileTable.addMouseListener(new SyncTableMouseListener());

        syncMouseMotionListener = new SyncMouseMotionListener(fileTable);
        fileTable.addMouseMotionListener(syncMouseMotionListener);

        root = new DefaultMutableTreeNode(fileTree.getRoot());
        viewTree = new JTree(root);
        viewTree.addTreeExpansionListener(this);
        viewTree.addTreeSelectionListener(this);
        viewTree.setCellRenderer(new SyncTreeCellRenderer());
        viewTree.addMouseListener(new SyncJTreeMouseListener());
        viewTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        tablePopup.addActionListener(this);
        treePopup.addActionListener(this);

        buildUpTree(root, fileTree.getRoot());
        // viewTree.setRootVisible(true);
        // viewTree.expandRow(0);
        // viewTree.setSelectionRow(0);

        leftScrollPane = new JScrollPane(viewTree);
        rightScrollPane = new JScrollPane(fileTable);

        splitPane = new JSplitPane();
        splitPane.setLeftComponent(leftScrollPane);
        splitPane.setRightComponent(rightScrollPane);
        splitPane.setPreferredSize(new Dimension(400, 400));
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setResizeWeight(0.3);
        // set border to null
        // this is quite important
        // see http://java.sun.com/docs/books/tutorial/uiswing/\
        // components/problems.html#nestedborders
        splitPane.setBorder(null);

        BorderLayout layout = new BorderLayout(2, 2);
        setLayout(layout);
        add(splitPane, BorderLayout.CENTER);
    }

    public synchronized void addSyncDiffAttributeListener(
            SyncDiffAttributeListener l) {
        if (syncMouseMotionListener != null) {
            syncMouseMotionListener.addSyncDiffAttributeListener(l);
        }
    }

    private void buildUpTree(final DefaultMutableTreeNode curRoot,
            final SyncFile curFile) {

        SyncFile[] childs = curFile.getChildren();
        final SyncFileSorter sorter = new SyncFileSorter();
        childs = sorter.sortByName(childs);
        for (int i = 0; i < childs.length; i++) {
            final SyncFile sf = childs[i];
            if (sf.getFile().isDirectory()) {
                final boolean viewable = isFileViewable(sf);
                if (viewable) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(sf);
                    curRoot.add(node);
                    buildUpTree(node, sf);
                }
            }
        }
    }

    private boolean isFileViewable(SyncFile sf) {
        boolean viewable = true;

        if (isDiffTree && !diffCol.contains(sf)) {
            viewable = false;
        }

        if (!showExcluded && sf.isExcluded()) {
            viewable = false;
        }

        return viewable;
    }

    /**
     * @param diffTree
     *            show differences only
     */
    public synchronized void setShowOnlyDifferences(final boolean diffTree) {
        isDiffTree = diffTree;
    }

    public synchronized boolean getShowOnlyDifferences() {
        return isDiffTree;
    }

    public synchronized void setShowExcludedFiles(final boolean showExcluded) {
        this.showExcluded = showExcluded;
    }

    public synchronized boolean getShowExcludedFiles() {
        return showExcluded;
    }

    /**
     * SyncTreeExplorer calls this method to select the first node in the tree.
     * This is important for updating the GUI correctly because the TreeViewer
     * is instantiated before the explorer adds itself as a listener.
     */
    public void showRootNode() {
        viewTree.setRootVisible(true);
        viewTree.expandRow(0);
        viewTree.setSelectionRow(0);
    }

    @SuppressWarnings("unchecked")
    protected void rebuildTree() {
        final Enumeration en = root.depthFirstEnumeration();
        while (en.hasMoreElements()) {
            ((DefaultMutableTreeNode) en.nextElement()).removeAllChildren();
        }

        buildUpTree(root, fileTree.getRoot());
        reloadTable();

        viewTree.updateUI();
        fileTable.updateUI();
        rightScrollPane.getVerticalScrollBar().setValue(0);
    }

    public void treeCollapsed(TreeExpansionEvent e) {
    }

    public void treeExpanded(TreeExpansionEvent e) {
    }

    public void valueChanged(TreeSelectionEvent e) {
        reloadTable();
        fileTable.updateUI();
    }

    protected void reloadTable() {

        fileTable.clearSelection();

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) viewTree
                .getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        SyncFile newDir = (SyncFile) node.getUserObject();
        // check if path still exists
        final boolean exists = nodeExists(root, newDir);
        if (exists) {
            fireSyncTreePathChangedEvent(new SyncTreeViewEvent(this, newDir
                    .getFile().getAbsoluteFileName()));

            final SyncFile[] childs = newDir.getChildren();
            final SyncFile[] filteredChilds = filterChilds(childs);

            tableModel.setTableData(filteredChilds);
        } else {
            tableModel.setTableData(new SyncFile[0]);
        }

    }

    private SyncFile[] filterChilds(SyncFile[] childs) {
        if (childs == null) {
            return null;
        }
        final java.util.List<SyncFile> filteredFileList = new ArrayList<SyncFile>();
        for (int i = 0; i < childs.length; i++) {
            if (isFileViewable(childs[i])) {
                filteredFileList.add(childs[i]);
            }
        }

        return (SyncFile[]) filteredFileList
                .toArray(new SyncFile[filteredFileList.size()]);
    }

    public synchronized void addSyncTreeViewListener(SyncTreeViewListener l) {
        syncTreeListeners.add(l);
    }

    public void fireSyncTreePathChangedEvent(final SyncTreeViewEvent e) {
        for (SyncTreeViewListener l : syncTreeListeners) {
            l.syncTreeViewPathChanged(e);
        }
    }

    private boolean nodeExists(DefaultMutableTreeNode node, SyncFile f) {
        boolean ret = false;
        if (node != null && f != null) {
            if (node.getUserObject().equals(f)) {
                ret = true;
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    ret = nodeExists((DefaultMutableTreeNode) node
                            .getChildAt(i), f);
                    if (ret) {
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(ACT_TABLE_IGNORE)) {
            int[] rows = fileTable.getSelectedRows();
            if (rows.length > 0) {
                String[] fnames = new String[rows.length];
                for (int i = 0; i < rows.length; i++) {
                    fnames[i] = ((SyncFile) tableModel.getValueAt(rows[i], 0))
                            .getFile().getAbsoluteFileName();
                }
                TEventMulticaster.getInstance().fireTEvent(this, -1,
                        new SyncIgnoreFileMessage(fileTree, fnames));
            }

        } else if (cmd.equals(ACT_TABLE_SHOW_ALL)
                || cmd.equals(ACT_TREE_SHOW_ALL)) {
            TEventMulticaster.getInstance().fireTEvent(this, -1,
                    new SyncUnhideFileMessage());

        } else if (cmd.equals(ACT_TABLE_IGNORE_OTHERS)) {
            int[] rows = fileTable.getSelectedRows();
            if (rows.length > 0) {
                String[] fnames = new String[rows.length];
                for (int i = 0; i < rows.length; i++) {
                    fnames[i] = ((SyncFile) tableModel.getValueAt(rows[i], 0))
                            .getFile().getAbsoluteFileName();
                }
                // keep fnames, hide others
                if (fnames.length > 0) {
                    TEventMulticaster.getInstance().fireTEvent(this, -1,
                            new SyncShowOnlyFilesMessage(fileTree, fnames));
                }
            }

        } else if (cmd.equals(ACT_TREE_IGNORE)
                || cmd.equals(ACT_TREE_IGNORE_OTHERS)) {
            if (viewTree.getSelectionCount() > 0) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) viewTree
                        .getLastSelectedPathComponent();
                if (node != null) {
                    SyncFile file = (SyncFile) node.getUserObject();
                    String[] fnames = new String[] { file.getFile()
                            .getAbsoluteFileName() };
                    if (cmd.equals(ACT_TREE_IGNORE)) {
                        TEventMulticaster.getInstance().fireTEvent(this, -1,
                                new SyncIgnoreFileMessage(fileTree, fnames));
                    } else {
                        TEventMulticaster.getInstance().fireTEvent(this, -1,
                                new SyncShowOnlyFilesMessage(fileTree, fnames));
                    }
                }
            }

        } else if (cmd.equals(ACT_FILE_DIFF)) {

            int[] rows = fileTable.getSelectedRows();
            if (rows.length > 0) {
                SyncFile firstSelectedFile = (SyncFile) tableModel.getValueAt(
                        rows[0], 0);
                TEventMulticaster.getInstance().fireTEvent(
                        this,
                        -1,
                        new SyncPluginMessage(firstSelectedFile, fileTree,
                                "diff"));
            }

        } else {
            LOGGER.warning("[BUG] unknown action command " + cmd);
        }

    }

    public void processEvent(TEvent e) {
        TMessage msg = e.getMessage();
        if (msg instanceof SyncTreeViewUpdateMessage) {
            rebuildTree();
        }
    }

    // ----------------------------------------------------------------------------

    class SyncTreeCellRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = 1L;
        private ImageIcon folderIcon;

        public SyncTreeCellRenderer() {
            folderIcon = TStyleChooser.getStyle().getFolderImageIcon();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {

            SyncFile file = (SyncFile) ((DefaultMutableTreeNode) value)
                    .getUserObject();

            JLabel fileLabel = null;

            String fileName = file.getFile().getFileName();

            if (file.getFile().isDirectory()) {
                if (folderIcon != null) {
                    fileLabel = new JLabel(fileName, folderIcon, JLabel.LEFT);
                } else {
                    fileLabel = new JLabel("+" + fileName);
                }
            } else {
                fileLabel = new JLabel(fileName);
            }

            fileLabel.setOpaque(true);

            if (sel) {
                fileLabel.setBackground(Color.blue);
                fileLabel.setForeground(Color.white);
            } else {
                if (diffCol.contains(file)) {
                    SyncDiff diff = diffCol.getSyncDiff(file);
                    fileLabel.setForeground(diff.getForeground());
                    fileLabel.setToolTipText(diff.getDescription());
                } else if (file.isExcluded()) {
                    fileLabel.setForeground(SyncColorSet
                            .getColor(SyncColorSet.COLOR_EXCLUDED));
                }
                fileLabel.setBackground(Color.WHITE);
            }

            return fileLabel;
        }
    }

    // ----------------------------------------------------------------------------

    class SyncTableMouseListener extends MouseAdapter {
        public SyncTableMouseListener() {
        }

        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                handleRightMouseClicks(e);
                popup(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup(e);
            }
        }

        private void popup(MouseEvent e) {
            tablePopup.show(e.getComponent(), e.getX(), e.getY());
        }

        private void handleRightMouseClicks(MouseEvent e) {
            Point p = e.getPoint();
            int row = fileTable.rowAtPoint(p);
            int col = fileTable.columnAtPoint(p);

            LOGGER.finer("row = " + row + " col = " + col);

            if ((col == -1) || (row == -1)) {
                return;
            }

            fileTable.setRowSelectionInterval(row, row);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    requestFocusInWindow();
                    fileTable.changeSelection(fileTable.getSelectedRow(), 0,
                            false, false);
                }
            });
        }

    } // end SyncTableMouseListener

    static class SyncMouseMotionListener extends MouseMotionAdapter {

        private final SyncTreeTable table;
        private final java.util.List<SyncDiffAttributeListener> syncDiffAttributeListeners = new ArrayList<SyncDiffAttributeListener>();

        public SyncMouseMotionListener(final SyncTreeTable table) {
            if (table == null) {
                throw new IllegalArgumentException("table is null");
            }
            this.table = table;
        }

        public void mouseMoved(MouseEvent e) {

            final int col = table.columnAtPoint(e.getPoint());
            final int row = table.rowAtPoint(e.getPoint());
            if (col != -1 && row != -1) {
                Object o = table.getModel().getValueAt(row, col);
                if (o instanceof SyncFile) {
                    fireSyncDiffAttributeEvent(new SyncDiffAttributeEvent(this,
                            (SyncFile) o));
                }
            }

        }

        public synchronized void addSyncDiffAttributeListener(
                SyncDiffAttributeListener l) {
            if (l != null) {
                syncDiffAttributeListeners.add(l);
            }
        }

        public synchronized void fireSyncDiffAttributeEvent(
                SyncDiffAttributeEvent e) {
            if (e == null) {
                return;
            }
            for (SyncDiffAttributeListener l : syncDiffAttributeListeners) {
                l.showSyncAttribute(e);
            }
        }

    }

    // ----------------------------------------------------------------------------

    class SyncJTreeMouseListener extends MouseAdapter {

        public SyncJTreeMouseListener() {
        }

        public void mousePressed(MouseEvent e) {
            int row = viewTree.getRowForLocation(e.getX(), e.getY());
            viewTree.setSelectionRow(row);

            if (e.isPopupTrigger()) {
                popup(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup(e);
            }
        }

        private void popup(MouseEvent e) {
            treePopup.show(e.getComponent(), e.getX(), e.getY());
        }
    } // end SyncJTreeMouseListener

    // ----------------------------------------------------------------------------

    class SyncViewPopupMenu extends JPopupMenu {

        final java.util.List<JMenuItem> menuList = new ArrayList<JMenuItem>();

        public final static int TYPE_TABLE = 0;
        public final static int TYPE_TREE = 1;
        private final int type;

        public SyncViewPopupMenu(int type) {
            this.type = type;
            init();
        }

        protected void init() {

            String cmd;

            cmd = (type == TYPE_TABLE) ? ACT_TABLE_IGNORE : ACT_TREE_IGNORE;
            addMenuItem(LanguageBundle.getInstance().getMessage(
                    "sync.treeview.ignore"), SyncTreeViewer.this, cmd);

            cmd = (type == TYPE_TABLE) ? ACT_TABLE_IGNORE_OTHERS
                    : ACT_TREE_IGNORE_OTHERS;
            addMenuItem(LanguageBundle.getInstance().getMessage(
                    "sync.treeview.ignore_others"), SyncTreeViewer.this, cmd);

            cmd = (type == TYPE_TABLE) ? ACT_TABLE_SHOW_ALL : ACT_TREE_SHOW_ALL;
            addMenuItem(LanguageBundle.getInstance().getMessage(
                    "sync.treeview.showall"), SyncTreeViewer.this, cmd);

            if (type == TYPE_TABLE) {
                add(new JSeparator());
                addMenuItem("diff", SyncTreeViewer.this, ACT_FILE_DIFF);
            }

        }

        protected void addMenuItem(final String label,
                final ActionListener listener, final String cmd) {
            final JMenuItem item = new JMenuItem(label);
            item.setActionCommand(cmd);
            add(item);
            menuList.add(item);
        }

        public void addActionListener(ActionListener l) {
            final Iterator<JMenuItem> it = menuList.iterator();
            while (it.hasNext()) {
                it.next().addActionListener(l);
            }
        }

    } // end SyncTablePopupMenu

}

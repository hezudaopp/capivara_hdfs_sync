/*
 * capivara - Java File Synchronization
 *
 * Copyright (C) 2003 Sascha Hunold <hunoldinho@users.sourceforge.net>
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
 */

package net.sf.jfilesync.gui.viewmodel;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sf.jfilesync.MainWin;
import net.sf.jfilesync.engine.TFileData;
import net.sf.jfilesync.engine.TFileDataSorter;
import net.sf.jfilesync.engine.TFileProperties;
import net.sf.jfilesync.event.TEvent;
import net.sf.jfilesync.event.TEventListener;
import net.sf.jfilesync.event.TEventMulticaster;
import net.sf.jfilesync.event.TMessage;
import net.sf.jfilesync.event.types.ControlCenterSwitchMessage;
import net.sf.jfilesync.event.types.TChdirMessage;
import net.sf.jfilesync.event.types.TDeleteMessage;
import net.sf.jfilesync.event.types.TSortFileMessage;
import net.sf.jfilesync.event.types.TStandardMessage;
import net.sf.jfilesync.gui.icons.TImageIconProvider;
import net.sf.jfilesync.prop.LanguageBundle;
import net.sf.jfilesync.settings.SettingsTypeException;
import net.sf.jfilesync.settings.TProgramSettings;
import net.sf.jfilesync.settings.TStyleChooser;
import net.sf.jfilesync.settings.TStyleInterface;


/**
 * future replacement of TFileDataTable
 * @author sascha
 *
 */

public class FileTable2 extends JTable implements TEventListener {

  private static final long serialVersionUID = -5086515441377804945L;

  protected TFileDataModel fileDataModel;

  private ButtonHeaderRenderer[] headerRenderer;
  protected int ccId;
  private ImageIcon upIcon, downIcon;
  protected ImageIcon folderIcon;
  private TStyleInterface style = TStyleChooser.getStyle();
  private int lastFocusedRow = -1;
  private DefaultTableCellRenderer nameCellRenderer;
  private boolean dirOpenWithDouble = true;

  private PermissionsColumn permColumn = null;

  private final static Logger LOGGER = Logger.getLogger(FileTable2.class
      .getName());

  public FileTable2(int ccId, TFileDataModel fileDataModel) {
    super(fileDataModel);
    //setSelectionModel(new FileTableListSelectionModel());
    this.ccId = ccId;
    this.fileDataModel = fileDataModel;
    nameCellRenderer = new TNameCellRenderer(style);
    init();
  }

//  public FileTable2(int ccId, TFileDataModel fileDataModel,
//      DefaultTableCellRenderer nameCellRenderer) {
//    super(fileDataModel);
//    this.ccId = ccId;
//    this.fileDataModel = fileDataModel;
//    this.nameCellRenderer = nameCellRenderer;
//
//    init();
//  }

  private void init() {

    TEventMulticaster emc = TEventMulticaster.getInstance();
    emc.addTEventListener(this, TMessage.ID.SETTINGS_CHANGED_MESSAGE);

    loadSettings();

    TImageIconProvider iprovider = TImageIconProvider.getInstance();
    upIcon = iprovider.getImageIcon(TImageIconProvider.ICON_ARROW_UP);
    downIcon = iprovider.getImageIcon(TImageIconProvider.ICON_ARROW_DOWN);
    folderIcon = iprovider.getImageIcon(TImageIconProvider.FOLDER_IMAGE);

    headerRenderer = new ButtonHeaderRenderer[fileDataModel.getColumnCount()];
    for (int i = 0; i < fileDataModel.getColumnCount(); i++) {

      headerRenderer[i] = new ButtonHeaderRenderer(fileDataModel, fileDataModel
          .getColumnNameAt(i));
      TableColumn col = this.getColumnModel().getColumn(i);

      col.setHeaderRenderer(headerRenderer[i]);

      // nameColumn will "always" be the first column
      // let's pray and setReorderingAllowed(false);
      DefaultTableCellRenderer cellRenderer = null;
      if (i == 0) {
        cellRenderer = nameCellRenderer;
      } else {
        cellRenderer = new FileAttributeCellRenderer(style);
      }
      col.setCellRenderer(cellRenderer);

      switch (fileDataModel.getColumnPropertyNameAt(i)) {
      case TFileProperties.TFILE_PROPERTY_SIZE:
        col.setPreferredWidth(20);
        break;
      case TFileProperties.TFILE_PROPERTY_NAME:
        col.setPreferredWidth(100);
        break;
      case TFileProperties.TFILE_PROPERTY_MOD_TIME:
        col.setPreferredWidth(50);
        break;
      }
    }

    setForeground(style.getTableForegroundColor());
    setBackground(style.getTableBackgroundColor());
    setSelectionForeground(style.getTableSelectionForegroundColor());
    setSelectionBackground(style.getTableSelectionBackGroundColor());
    setShowGrid(style.showTableGrid());

    getTableHeader().setReorderingAllowed(false);

    JTableHeader header = this.getTableHeader();
    header.addMouseListener(new HeaderListener(header, headerRenderer));

    // tooltips off -> faster rendering
    ToolTipManager.sharedInstance().unregisterComponent(this);

    setRowSelectionAllowed(false);
    setColumnSelectionAllowed(false);
    setCellSelectionEnabled(true);

    unregisterAllKeyEvents();
    
    registerKeyEvents();

    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        final int row = FileTable2.this.rowAtPoint(e.getPoint());

        if ((e.getClickCount() > 1 && dirOpenWithDouble) || !dirOpenWithDouble) {

          if (!dirOpenWithDouble) {
            if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
              return;
            }
          }

          if (row == 0) {
            TEventMulticaster.getInstance().fireTEvent(FileTable2.this,
                FileTable2.this.ccId, new TChdirMessage(".."));
          } else {
            TFileProperties fileProp = (TFileProperties) FileTable2.this.fileDataModel
                .getValueAt(row, 0);
            if (fileProp.isDirectory()) {
              TEventMulticaster.getInstance().fireTEvent(FileTable2.this,
                  FileTable2.this.ccId,
                  new TChdirMessage(fileProp.getAbsoluteFileName()));
            }
          }
        }
      }

    });

  }

  private void unregisterAllKeyEvents() {

    InputMap tableInputMap =  (InputMap)UIManager.get("Table.ancestorInputMap");
    ActionMap tableActionMap =  (ActionMap)UIManager.get("Table.actionMap");
    
    for(final KeyStroke ks : tableInputMap.keys()) {
      final Object actionKey = tableInputMap.get(ks);
      tableInputMap.remove(ks);
      tableActionMap.remove(actionKey);
    }
    
    
//    LOGGER.info("mouse listeners : " + getMouseListeners().length);
//    for(MouseListener l : getMouseListeners()) {
//      LOGGER.info("remove : " + l);
//      removeMouseListener(l);
//    }
    
  }

  public void processEvent(TEvent e) {
    final TMessage.ID msgType = e.getMessage().getMessageType();
    switch (msgType) {
    case SETTINGS_CHANGED_MESSAGE:
      loadSettings();
      break;
    }
  }

  protected void loadSettings() {
    try {
      dirOpenWithDouble = MainWin.config.getProgramSettings().getBooleanOption(
          TProgramSettings.OPTION_DIR_OPEN_DOUBLE_CLICK);
    } catch (SettingsTypeException ste) {
      LOGGER.log(Level.WARNING, ste.getMessage());
    }
  }

  public int getCurrentColumnFileProperty(int column) {
    int properties[] = TFileProperties.getPublishableProperties();
    return properties[column];
  }

  // ---------------------------------------------------------------------------

  public TFileProperties[] getSelectedFileData() {
    int[] rows = getSelectedRows();

    //logger.debug("rows selected : " + getSelectedRowCount());

    if (rows.length == 0) {
      LOGGER.info("nothing selected");
      return null;
    }

    int rowsToCopy = rows.length;

    // ".." does not count
    if (getSelectedRow() == 0) {
      rowsToCopy--;
    }

    TFileProperties[] props = new TFileProperties[rowsToCopy];

    for (int i = 0, f = 0; i < rows.length; i++) {
      if (rows[i] != 0) {
        props[f++] = (TFileProperties) fileDataModel
            .getFilePropertiesAtRow(rows[i]);
      }
    }
    return props;
  }

  // ---------------------------------------------------------------------------

  public TFileProperties getFirstSelectedFileData() {
    TFileProperties back = null;

    if (getSelectedRowCount() > 0 && getRowCount() > 1) {
      int row = getSelectedRow();
      if (row > 0) {
        back = (TFileProperties) fileDataModel.getFilePropertiesAtRow(row);
      }
    }
    return back;
  }

  public void deselectAllFiles() {
    if (getRowCount() > 0) {
      setRowSelectionInterval(0, 0);
    }
    clearSelection();
  }

  public void selectAllFiles() {
    this.selectAll();
  }

  public void saveSelectedRow() {
    lastFocusedRow = getSelectedRow();
  }

  public void restoreSelectedRow(String lastSelectedFile) {

    if (lastSelectedFile == null) {
      lastFocusedRow = -1;
    } else {
      int row2select = fileDataModel.getRowOfFile(lastSelectedFile);
      if (row2select == -1) {
        lastFocusedRow = -1;
      } else {
        lastFocusedRow = row2select;
      }
    }
    restoreSelectedRow();

  }

  public void restoreSelectedRow() {
    if (lastFocusedRow != -1) {
      setColumnSelectionInterval(0, 0);
      //logger.debug("select : " + lastFocusedRow);
      if (lastFocusedRow < getRowCount()) {
        setRowSelectionInterval(lastFocusedRow, lastFocusedRow);
      } else {
        lastFocusedRow = 0;
        setRowSelectionInterval(0, 0);
      }
      // logger.debug("ccid : " + ccId + " : restore row :" + lastFocusedRow);
    } else {
      setColumnSelectionInterval(0, 0);
      setRowSelectionInterval(0, 0);
      lastFocusedRow = 0;
    }
  }

  public void selectRow(int row) {
    if (row >= 0 && row < getRowCount()) {
      setColumnSelectionInterval(0, 0);
      setRowSelectionInterval(row, row);
      lastFocusedRow = row;
    }
  }

  public void reinitSelection() {
    if (getRowCount() > 0 && getColumnCount() > 0) {
      setColumnSelectionInterval(0, 0);
      setRowSelectionInterval(0, 0);
      lastFocusedRow = 0;
    }
  }

  public void setDataModel(TFileDataModel fileDataModel) {
    this.fileDataModel = fileDataModel;
  }

  public int getRowOfFile(String fileName) {
    int row = -1;
    if (fileDataModel != null && !fileName.equals("")) {
      row = fileDataModel.getRowOfFile(fileName);
    }
    return row;
  }

  private void registerKeyEvents() {
    addKeyAction("switch-browser");
    addKeyAction("chdir");
    addKeyAction("copy");
    addKeyAction("select-file");
    addKeyAction("delete");
    addKeyAction("browse-next");
    addKeyAction("browse-prev");
    
    addCustomKeyEvent("end-key");
    addCustomKeyEvent("home-key");
    addCustomKeyEvent("arrow-down");
    addCustomKeyEvent("arrow-up");
  }

  /*
   * register custom key events with table
   * e.g. End and Home keys
   */
  private void addCustomKeyEvent(final String key) {
     
    KeyStroke ks = null;
    
    if( key.equals("end-key") ) {
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0);
    } else if( key.equals("home-key") ) {
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0);
    } else if (key.equals("arrow-down")) {
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
    } else if (key.equals("arrow-up")) {
      ks = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
    }

    if( ks != null ) {
      getInputMap().put(ks, key);
      getActionMap().put(key, new TableKeyAction(key, this));
    } else {
      LOGGER.warning("unkown key event : " + key);
    }

  }

  /*
   * registers user-defined shortcuts with the table
   */
  private void addKeyAction(String shortcut) {
    final java.util.List<KeyStroke> ksList = MainWin.keyMap
        .getKeyStrokeList(shortcut);

    if (ksList != null) {
      for(final KeyStroke ks : ksList ) {
        getInputMap().put(ks, shortcut);
        getActionMap().put(shortcut, new TableKeyAction(shortcut, this));
      }
    } else {
      LOGGER.warning("Shortcut : " + shortcut + " is unkown");
    }
  }

  public void revalidateColumns() {
    int modelCols = fileDataModel.getColumnCount();
    int cols = getColumnCount();
    if (cols < modelCols) {
      if (permColumn == null) {
        permColumn = new PermissionsColumn(3, 30);
      }
      addColumn(permColumn);
    } else if (modelCols == 3) {
      removeColumn(permColumn);
    }
  }

  //---------------------------------------------------------------------------

  class TableKeyAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private Object source;
    private String cmd;

    public TableKeyAction(String cmd, Object source) {
      this.cmd = cmd;
      this.source = source;
    }

    public void actionPerformed(ActionEvent e) {

      if (cmd.equals("switch-browser")) {
        TEventMulticaster.getInstance().fireTEvent(source, ccId,
            new ControlCenterSwitchMessage(ccId));
      } else if (cmd.equals("chdir")) {
        chdir_event();
      } else if (cmd.equals("copy")) {
        if (getSelectedRowCount() > 0) {
          TEventMulticaster.getInstance().fireTEvent(source, ccId,
              new TStandardMessage(TMessage.ID.COPY_FILES_MESSAGE));
        }
      } else if (cmd.equals("select-file")) {
        int[] selected = getSelectedRows();
        if (selected == null) {
          setRowSelectionInterval(getSelectedRow(), getSelectedRow());
        } else {
          int lastSelected = selected[selected.length - 1];
          if (lastSelected < getRowCount() - 1) {
            addRowSelectionInterval(lastSelected + 1, lastSelected + 1);
          }
        }
      } else if (cmd.equals("delete")) {
        TEventMulticaster.getInstance().fireTEvent(source, ccId,
            new TDeleteMessage());
      } else if (cmd.equals("reload")) {
        TEventMulticaster.getInstance().fireTEvent(FileTable2.this, ccId,
            new TStandardMessage(TMessage.ID.RELOAD_DIR_MESSAGE));
      } else if (cmd.equals("browse-next")) {
        chdir_event();
      } else if (cmd.equals("browse-prev")) {
        TEventMulticaster.getInstance().fireTEvent(FileTable2.this, ccId,
            new TChdirMessage(".."));
      } else if( cmd.equals("end-key") ) {
        selectRow(getRowCount()-1);
      } else if( cmd.equals("home-key") ) {
        selectRow(0);
      } else if( cmd.equals("arrow-down") ) {
        System.out.println("arrow-down");
        selectRow(Math.min(getSelectedRow()+1, getRowCount()-1));
      } else if( cmd.equals("arrow-up") ) {
        System.out.println("arrow-up");
        selectRow(Math.max(getSelectedRow()-1, 0));
      } else {
        LOGGER.warning("unhandled key event " + cmd);        
      }
    }

  } // end TableKeyAction

  // ---------------------------------------------------------------------------

  private void chdir_event() {
    LOGGER.info("selected row : " + getSelectedRow());
    if (getSelectedRow() == 0) {
      TEventMulticaster.getInstance().fireTEvent(FileTable2.this, ccId,
          new TChdirMessage(".."));
    } else {
      int srow = getSelectedRow();
      if (srow >= 0 && srow < getRowCount()) {
        TFileProperties fileProp = (TFileProperties) FileTable2.this.fileDataModel
            .getValueAt(getSelectedRow(), 0);
        if (fileProp != null && fileProp.isDirectory()) {
          TEventMulticaster.getInstance().fireTEvent(FileTable2.this, ccId,
              new TChdirMessage(fileProp.getAbsoluteFileName()));
        }
      }
    }
  }

  // ---------------------------------------------------------------------------

  class ButtonHeaderRenderer extends JButton implements TableCellRenderer {

    private static final long serialVersionUID = 1632587033059383895L;
    private TFileDataModel fileDataModel;
    private int pushedColumn;

    public ButtonHeaderRenderer(TFileDataModel fileDataModel, String label) {
      super(label);
      this.fileDataModel = fileDataModel;
      this.setHorizontalAlignment(JButton.LEFT);
      this.setHorizontalTextPosition(JButton.LEFT);
      pushedColumn = -1;
      setMargin(new Insets(0, 0, 0, 0));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      boolean isPressed = (column == pushedColumn);
      String buttonLabel = fileDataModel.getColumnNameAt(column);
      TFileData fileData = fileDataModel.getFileData();

      int colProp = fileData.getPropertyAtColumn(column);

      if (colProp == fileData.getCurrentSortedProperty()) {
        ImageIcon iconToUse = upIcon;
        int direction;
        if ((direction = fileData.getLastSortDirection()) != -1) {
          if (direction == TFileDataSorter.SORT_ASCENDING) {
            iconToUse = downIcon;
          }
          this.setText(buttonLabel);
          this.setIcon(iconToUse);
        }
      } else {
        setIcon(null);
        setText(buttonLabel);
      }

      getModel().setPressed(isPressed);
      getModel().setArmed(isPressed);
      return this;
    }

    public void setPressedColumn(int col) {
      pushedColumn = col;
    }

  }

  class HeaderListener extends MouseAdapter {

    private JTableHeader header;
    private ButtonHeaderRenderer renderer[];
    private static final int offset = 3;
    private Rectangle r;
    private TableColumnModel colModel;

    HeaderListener(JTableHeader header, ButtonHeaderRenderer renderer[]) {
      this.header = header;
      this.renderer = renderer;
      r = new Rectangle();
      colModel = header.getColumnModel();
    }

    public void mousePressed(MouseEvent e) {
      Point p = e.getPoint();
      int col = header.columnAtPoint(p);
      int colXOffset = 0;

      FileTable2.this.clearSelection();

      if (col >= renderer.length) {
        return;
      }

      //      System.out.println("MouseClick at " + e.getPoint());

      for (int i = 0; i < col; i++) {
        colXOffset += colModel.getColumn(i).getWidth();
      }

      TableColumn column = header.getColumnModel().getColumn(col);
      int cw = column.getWidth();
      int ch = header.getHeight();

      //      System.out.println("cw = "+ cw + " ch = " + ch);

      r.setBounds(offset + colXOffset, offset, cw-2*offset, ch-2*offset);
      if (!r.contains(p)) {
        return;
      }

      renderer[col].setPressedColumn(col);
      header.repaint();
      TEventMulticaster emc = TEventMulticaster.getInstance();
      emc.fireTEvent(FileTable2.this, FileTable2.this.ccId,
          new TSortFileMessage(getCurrentColumnFileProperty(col)));
    }

    public void mouseReleased(MouseEvent e) {
      int col = header.columnAtPoint(e.getPoint());
      if (col < renderer.length) {
        renderer[col].setPressedColumn(-1);
        header.repaint();
      }
    }
  }

  class PermissionsColumn extends TableColumn {
    public PermissionsColumn(int index, int width) {
      super(index, width);
      setHeaderRenderer(new PermissionHeaderRenderer());
      setCellRenderer(new FileAttributeCellRenderer(style));
    }
  }

  class PermissionHeaderRenderer implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      JButton headerLabel = new JButton(LanguageBundle.getInstance()
          .getMessage("table.header.permissions"));
      return headerLabel;
    }
  }

} // end TFileDataTable

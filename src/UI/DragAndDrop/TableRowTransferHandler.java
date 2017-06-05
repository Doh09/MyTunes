/*
 * the tranfer handler to drag and drop
 */
package UI.DragAndDrop;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class TableRowTransferHandler extends TransferHandler
{
    private final DataFlavor localObjectFlavor = new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index");
    private JTable playlistTable = null;

    /**
     * constructor
     * @param playlistTable
     */
    public TableRowTransferHandler(JTable playlistTable)
    {
        this.playlistTable = playlistTable;
    }

    /**
     * gets the selected row of the object
     * @param c
     * @return
     */
    @Override
    protected Transferable createTransferable(JComponent c)
    {
       assert (c == playlistTable);
       return new DataHandler(new Integer(playlistTable.getSelectedRow()), localObjectFlavor.getMimeType());
          
    }

    /**
     * checks if its possible to drop the object on the given JTable
     * @param info
     * @return
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport info)
    {
       boolean b = info.getComponent() == playlistTable && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
       playlistTable.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
       return b;
    }

    /**
     * tells the transferHandler what type of drop is permitted
     * @param c
     * @return
     */
    @Override
    public int getSourceActions(JComponent c)
    {
        return TransferHandler.MOVE;
    }

    /**
     * checks if its acceptable to drop the object in the cursor row
     * @param info
     * @return
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport info)
    {   
        JTable target = (JTable) info.getComponent();
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int index = dl.getRow();
        int max = playlistTable.getModel().getRowCount();
        if (index < 0 || index > max)
            index = max;
        target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try{
            Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
            if (rowFrom != -1 && rowFrom != index){
                ((Reorderable) playlistTable.getModel()).reorder(rowFrom, index);
                if(index > rowFrom){
                    index--;
                    target.getSelectionModel().addSelectionInterval(index, index);
                    return true;
                }
            }
        }catch(Exception e)
        {
           e.printStackTrace();
        }
        return false;
    }

    /**
     * Export done
     * @param c
     * @param t
     * @param act
     */
    @Override
    protected void exportDone(JComponent c, Transferable t, int act)
    {
        if((act == TransferHandler.MOVE || act == TransferHandler.NONE)){
        playlistTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

  

    
}
    
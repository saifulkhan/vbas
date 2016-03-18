package uk.ac.isc.hypomagnitudeview;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * Top component which displays the network magnitudes.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.hypomagnitudeview//HypoMagnitudeView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "HypoMagnitudeViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "magnitudeview", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.hypomagnitudeview.HypoMagnitudeViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_HypoMagnitudeViewAction",
        preferredID = "HypoMagnitudeViewTopComponent"
)
@Messages({
    "CTL_HypoMagnitudeViewAction=Hypocentre Magnitudes",
    "CTL_HypoMagnitudeViewTopComponent=Hypocentre Magnitudes",
    "HINT_HypoMagnitudeViewTopComponent=Hypocentre Magnitudes"
})
public final class HypoMagnitudeViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private final JScrollPane scrollPane;
    // the panel to have the figure
    HypoMagnitudeViewPanel hmag = null;

    // Data
    private final HypocentresList hyposList = Global.getHypocentresList();
    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();

    public HypoMagnitudeViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoMagnitudeViewTopComponent());
        setToolTipText(Bundle.HINT_HypoMagnitudeViewTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        //setName("Hypocentre Magnitudes");

        VBASLogger.logDebug("Loaded...");

        selectedSeisEvent.addChangeListener(this);

        hmag = new HypoMagnitudeViewPanel(hyposList.getHypocentres());
        scrollPane = new JScrollPane(hmag);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    //repaint the view when data chages
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        String eventName = event.getData().getClass().getName();
        VBASLogger.logDebug(" Event received from " + eventName);

        // It only received SeiesEvent selected/changed now
        switch (eventName) {
            case ("uk.ac.isc.seisdata.SeisEvent"):
                //SeisEvent seisEvent = (SeisEvent) event.getData();
                VBASLogger.logDebug("SeisEvent= " + selectedSeisEvent.getEvid());

                hmag.updateData(hyposList.getHypocentres());
                hmag.repaint();
                scrollPane.setViewportView(hmag);

                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        //hyposList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        //hyposList.removeChangeListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}

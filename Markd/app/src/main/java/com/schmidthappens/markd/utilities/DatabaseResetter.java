package com.schmidthappens.markd.utilities;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.data_objects.TempCustomerData;

/**
 * Created by joshua.schmidtibm.com on 11/18/17.
 */

public class DatabaseResetter {
    public static void resetDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userReference = database.getReference("users");
        DatabaseReference zipCodesReference = database.getReference("zip_codes");

        //Plumber
        userReference.child("defaultPlumberOne")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "SDR Plumbing & Heating",
                                "(203) 348-2295",
                                "http://www.sdrplumbing.com",
                                "06902"
                        )));
        zipCodesReference.child("06902").child("defaultPlumberOne").setValue("Plumber");
        userReference.child("defaultPlumberTwo")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "Rick's Plumbing Service, Inc",
                                "(203) 516-4542",
                                "http://www.badaraccoplumbing.com/",
                                "06460"
                        )));
        zipCodesReference.child("06460").child("defaultPlumberTwo").setValue("Plumber");
        userReference.child("defaultPlumberThree")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "Badaracco Plumbing & Heating",
                                "(203) 223-1579",
                                "http://www.badaraccoplumbing.com/",
                                "06851"
                        )));
        zipCodesReference.child("06851").child("defaultPlumberThree").setValue("Plumber");
        userReference.child("defaultPlumberFour")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "Botticelli Plumbing & Heating",
                                "(203) 794-9297",
                                "http://www.botticelliplumbing.com/",
                                "06811"
                        )));
        zipCodesReference.child("06811").child("defaultPlumberFour").setValue("Plumber");

        //HVAC
        userReference.child("defaultHvacOne")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Aireserv",
                                "(203) 348-2295",
                                "http://www.aireserv.com",
                                "06776"
                        )));
        zipCodesReference.child("06776").child("defaultHvacOne").setValue("Hvac");
        userReference.child("defaultHvacTwo")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Steve Basso Plumbing, Heating & A/C",
                                "(203) 335-0224",
                                "http://www.bassophac.com",
                                "06610"
                        )));
        zipCodesReference.child("06610").child("defaultHvacTwo").setValue("Hvac");
        userReference.child("defaultHvacThree")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Aquila Heating & Cooling, LLC",
                                "(203) 334-4880",
                                "http://www.aquilaheatingandcooling.com",
                                "06824"
                        )));
        zipCodesReference.child("06824").child("defaultHvacThree").setValue("Hvac");
        userReference.child("defaultHvacFour")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Tyler Heating, Air Conditioning, and Refrigeration",
                                "(203) 378-4700",
                                "http://www.tylerair.com",
                                "06461"
                        )));
        zipCodesReference.child("06461").child("defaultHvacFour").setValue("Hvac");

        //Electrician
        userReference.child("defaultElectricianOne")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "ConnWest Electric",
                                "(203) 922-2011",
                                "http://www.connwestelectric.com",
                                "06870"
                        )));
        zipCodesReference.child("06870").child("defaultElectricianOne").setValue("Electrician");
        userReference.child("defaultElectricianTwo")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "Ferrer's Electric LLC",
                                "(203) 267-0723",
                                "http://www.ferrers-electric.com",
                                "06488"
                        )));
        zipCodesReference.child("06488").child("defaultElectricianTwo").setValue("Electrician");
        userReference.child("defaultElectricianThree")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "Wilton Electrical Services Inc.",
                                "(203) 834-9715",
                                "http://www.wiltonelectricalservices.com",
                                "06897"
                        )));
        zipCodesReference.child("06897").child("defaultElectricianThree").setValue("Electrician");
        userReference.child("defaultElectricianFour")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "Mazzucco Electric",
                                "(203) 257-0297",
                                "http://www.mazzuccoelectric.com",
                                "06825"
                        )));
        zipCodesReference.child("06825").child("defaultElectricianFour").setValue("Electrician");

        //Painter
        userReference.child("defaultPainterOne")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "MDF Painting & Power Washing",
                                "(203) 348-2295",
                                "http://www.mdfpainting.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultPainterOne").setValue("Painter");
        userReference.child("defaultPainterTwo")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "Wright Painting and Remodeling",
                                "(203) 221-9005",
                                "http://www.wrightpainting.info",
                                "06880"
                        )));
        zipCodesReference.child("06880").child("defaultPainterTwo").setValue("Painter");
        userReference.child("defaultPainterThree")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "Shoreline Painting & Drywall, Inc.",
                                "(203) 302-1086",
                                "http://www.shorelinepaintingct.com",
                                "06851"
                        )));
        zipCodesReference.child("06851").child("defaultPainterThree").setValue("Painter");
        userReference.child("defaultPainterFour")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "Steve Zeisler’s Painting",
                                "(203) 254-2751",
                                "http://www.stevezeislerspainting.com/",
                                "06824"
                        )));
        zipCodesReference.child("06824").child("defaultPainterFour").setValue("Painter");

        //Architect
        userReference.child("defaultArchitectOne")
                .setValue(new Contractor()
                        .setType("Architect")
                        .setContractorDetails(new ContractorDetails(
                                "Granoff Architects",
                                "(203) 625-9460",
                                "http://www.granoffarchitects.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultArchitectOne").setValue("Architect");
        userReference.child("defaultArchitectTwo")
                .setValue(new Contractor()
                        .setType("Architect")
                        .setContractorDetails(new ContractorDetails(
                                "Joeb Moore & Partners",
                                "(203) 769-5828",
                                "http://joebmoore.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultArchitectTwo").setValue("Architect");
        userReference.child("defaultArchitectThree")
                .setValue(new Contractor()
                        .setType("Architect")
                        .setContractorDetails(new ContractorDetails(
                                "Shoreline Design Group",
                                "(203) 661-3200",
                                "http://shorelinedesign.net",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultArchitectThree").setValue("Architect");
        userReference.child("defaultArchitectFour")
                .setValue(new Contractor()
                        .setType("Architect")
                        .setContractorDetails(new ContractorDetails(
                                "Wadia Associates",
                                "(203) 966-0048",
                                "http://www.wadiaassociates.com",
                                "06840"
                        )));
        zipCodesReference.child("06840").child("defaultArchitectFour").setValue("Architect");

        //Builder
        userReference.child("defaultBuilderOne")
                .setValue(new Contractor()
                        .setType("Builder")
                        .setContractorDetails(new ContractorDetails(
                                "LOPARCO",
                                "(203) 629-4800",
                                "http://loparco.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultBuilderOne").setValue("Builder");
        userReference.child("defaultBuilderTwo")
                .setValue(new Contractor()
                        .setType("Builder")
                        .setContractorDetails(new ContractorDetails(
                                "Davenport Contracting",
                                "(203) 324-6308",
                                "https://www.davenportcontracting.com",
                                "06902"
                        )));
        zipCodesReference.child("06902").child("defaultBuilderTwo").setValue("Builder");
        userReference.child("defaultBuilderThree")
                .setValue(new Contractor()
                        .setType("Builder")
                        .setContractorDetails(new ContractorDetails(
                                "RR Builders",
                                "(203) 972-6100",
                                "http://www.rrbuilders.com",
                                "06840"
                        )));
        zipCodesReference.child("06840").child("defaultBuilderThree").setValue("Builder");
        userReference.child("defaultBuilderFour")
                .setValue(new Contractor()
                        .setType("Builder")
                        .setContractorDetails(new ContractorDetails(
                                "Hobbs Incorporated",
                                "(203) 966-0726",
                                "http://www.hobbsinc.com",
                                "06840"
                        )));
        zipCodesReference.child("06840").child("defaultBuilderFour").setValue("Builder");

        //Realtor
        userReference.child("defaultRealtorOne")
                .setValue(new Contractor()
                        .setType("Realtor")
                        .setContractorDetails(new ContractorDetails(
                                "Anderson Associates",
                                "(203) 629-4519",
                                "http://greenwichliving.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultRealtorOne").setValue("Realtor");
        userReference.child("defaultRealtorTwo")
                .setValue(new Contractor()
                        .setType("Realtor")
                        .setContractorDetails(new ContractorDetails(
                                "David Oglivy & Associates",
                                "(203) 869-9866",
                                "http://www.davidogilvy.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultRealtorTwo").setValue("Realtor");
        userReference.child("defaultRealtorThree")
                .setValue(new Contractor()
                        .setType("Realtor")
                        .setContractorDetails(new ContractorDetails(
                                "Berkshire Hathaway",
                                "(203) 869-0500",
                                "https://www.bhhsneproperties.com",
                                "06830"
                        )));
        zipCodesReference.child("06830").child("defaultRealtorThree").setValue("Realtor");
        userReference.child("defaultRealtorFour")
                .setValue(new Contractor()
                        .setType("Realtor")
                        .setContractorDetails(new ContractorDetails(
                                "Kathy Tanner",
                                "(203) 966-3507",
                                "http://kathytanner.houlihanlawrence.com",
                                "06840"
                        )));
        zipCodesReference.child("06840").child("defaultRealtorFour").setValue("Realtor");

        //Default Customer
        userReference.child("R1EyDxbQK3Uf8ohZDNrazgM4FVc2")
                .setValue(TempCustomerData.makeCustomer());
    }
}

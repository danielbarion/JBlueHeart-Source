package gr.sr.dressmeEngine.xml.dataParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilderFactory;

import l2r.Config;

import gr.sr.data.xml.AbstractFileParser;
import gr.sr.dressmeEngine.data.DressMeArmorData;
import gr.sr.dressmeEngine.xml.dataHolder.DressMeArmorHolder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public final class DressMeArmorParser extends AbstractFileParser<DressMeArmorHolder>
{
	private final String ARMOR_FILE_PATH = Config.DATAPACK_ROOT + "/data/xml/sunrise/dressme/armor.xml";
	
	private static final DressMeArmorParser _instance = new DressMeArmorParser();
	
	public static DressMeArmorParser getInstance()
	{
		return _instance;
	}
	
	private DressMeArmorParser()
	{
		super(DressMeArmorHolder.getInstance());
	}
	
	@Override
	public File getXMLFile()
	{
		return new File(ARMOR_FILE_PATH);
	}
	
	@Override
	protected void readData()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		
		File file = getXMLFile();
		
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if (n.getNodeName().equalsIgnoreCase("list"))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equalsIgnoreCase("dress"))
						{
							int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
							String name = d.getAttributes().getNamedItem("name").getNodeValue();
							String type = d.getAttributes().getNamedItem("type").getNodeValue();
							
							int itemId = 0;
							long itemCount = 0;
							int chest = 0;
							int legs = 0;
							int gloves = 0;
							int feet = 0;
							
							for (Node att = d.getFirstChild(); att != null; att = att.getNextSibling())
							{
								if ("set".equalsIgnoreCase(att.getNodeName()))
								{
									chest = Integer.parseInt(att.getAttributes().getNamedItem("chest").getNodeValue());
									legs = Integer.parseInt(att.getAttributes().getNamedItem("legs").getNodeValue());
									gloves = Integer.parseInt(att.getAttributes().getNamedItem("gloves").getNodeValue());
									feet = Integer.parseInt(att.getAttributes().getNamedItem("feet").getNodeValue());
								}
								
								if ("price".equalsIgnoreCase(att.getNodeName()))
								{
									itemId = Integer.parseInt(att.getAttributes().getNamedItem("id").getNodeValue());
									itemCount = Long.parseLong(att.getAttributes().getNamedItem("count").getNodeValue());
								}
							}
							
							getHolder().addDress(new DressMeArmorData(id, name, type, chest, legs, gloves, feet, itemId, itemCount));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warn(getClass().getSimpleName() + ": Error: " + e);
		}
	}
}
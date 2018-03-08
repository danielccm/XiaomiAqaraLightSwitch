/**
 *  Xiaomi Aqara Light Switch (Zigbee)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 * 
 *  Based on original DH by a4refillpad 2017
 *  Based on original DH by Eric Maycock 2015 and Rave from Lazcad
 *  change log:
 *  modified to allow button capability
 *
 *  Ver 1.0  -  9-12-2017
 *    Converted to support button presses with Xiaomi Zigbee Aqara Light Switch (Single Switch)
 *
 *
 *  Fingerprint Endpoint data:
 *  zbjoin: {"dni":"xxxx","d":"xxxxxxxxxxx","capabilities":"80","endpoints":[{"simple":"01 0104 5F01 01 03 0000 FFFF 0006 03 0000 0004 FFFF","application":"03","manufacturer":"LUMI","model":"lumi.sensor_switch.aq2"}],"parent":"0000","joinType":1}
 *     endpoints data
 *        01 - endpoint id
 *        0104 - profile id
 *        5F01 - device id
 *        01 - ignored
 *        03 - number of in clusters
 *        0000 ffff 0006 - inClusters
 *        03 - number of out clusters
 *        0000 0004 ffff - outClusters
 *        manufacturer "LUMI" - must match manufacturer field in fingerprint
 *        model "lumi.sensor_switch.aq2" - must match model in fingerprint
 *        deviceJoinName: whatever you want it to show in the app as a Thing
 *
 */
preferences {
	input name: "dateformat", type: "enum", title: "Set Date Format\n US (MDY) - UK (DMY) - Other (YMD)", description: "Date Format", required: false, options:["BG","US","UK","Other"]
	input description: "Only change the settings below if you know what you're doing", displayDuringSetup: false, type: "paragraph", element: "paragraph", title: "ADVANCED SETTINGS"
	input name: "voltsmax", title: "Max Volts\nA battery is at 100% at __ volts\nRange 2.8 to 3.4", type: "decimal", range: "2.8..3.4", defaultValue: 3, required: false
	input name: "voltsmin", title: "Min Volts\nA battery is at 0% (needs replacing) at __ volts\nRange 2.0 to 2.7", type: "decimal", range: "2..2.7", defaultValue: 2.5, required: false
}  
 
metadata {
	definition (name: "Xiaomi Aqara Light Switch ON/OFF", namespace: "XiaomiSwitch", author: "enchoss") {	
    	capability "Battery"
		capability "Button"
		capability "Actuator"
		capability "Switch"
		capability "Momentary"
		capability "Configuration"
		capability "Sensor"
		capability "Refresh"
        //capability "Battery"
        //capability "Health Check"
        
		attribute "lastPress", "string"
        attribute "lastPressDate", "Date"
		attribute "lastCheckin", "string"
        attribute "lastCheckinDate", "Date"
        attribute "batteryRuntime", "String"
        
        command "resetBatteryRuntime"
        //lumi.sensor_86sw1 //
        fingerprint endpointId: "01", inClusters: "0000,FFFF,0006", outClusters: "0000,0004,FFFF"
    	//fingerprint endpointId: "01", profileId: "0104", deviceId: "5F01", inClusters: "0000,FFFF,0006", outClusters: "0000,0004,FFFF", manufacturer: "LUMI", model: "lumi.sensor_switch.aq2", deviceJoinName: "Xiaomi Aqara Light Switch"
	}
    
    	simulator {
  		status "button pressed": "on/off: 1"
        status "button released": "on/off: 0"
    	}

	tiles(scale: 2) {
		
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
           		    attributeState("pushed", label:' push', action: "momentary.push", backgroundColor:"#53a7c0")
                    attributeState("released", label:' push', action: "momentary.push", backgroundColor:"#ffffff", nextState: "pushed") 
           			
            		  
 			}
            		tileAttribute("device.lastPress", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'Last Pressed: ${currentValue}')
            		}
		}        
       
        valueTile("battery", "device.battery", decoration:"flat", inactiveLabel: false, width: 2, height: 2) {
            state "default", label:'${currentValue}%', unit:"%", 
            backgroundColors:[
                [value: 10, color: "#bc2323"],
                [value: 26, color: "#f1d801"],
                [value: 51, color: "#44b621"]
            ]
        }
        
        valueTile("batteryVolts", "device.batteryVolts", decoration:"flat", inactiveLabel: false, width: 2, height: 2) {
            state "default", label:'${currentValue}v', unit:"v",  
            backgroundColors:[
                [value: 1, color: "#bc2323"],
                [value: 2, color: "#f1d801"],
                [value: 3, color: "#44b621"]
            ]
        }
		
        	standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        	}
            
            valueTile("lastcheckin", "device.lastCheckin", decoration: "flat", inactiveLabel: false, width: 4, height: 2) {
            state "default", label:'Last Checkin:\n${currentValue}',icon: "st.Health & Wellness.health9"
           }
		
         valueTile("batteryRuntime", "device.batteryRuntime", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
           state "batteryRuntime", label:'Battery Changed\n (tap to reset):\n ${currentValue}', unit:"", action:"resetBatteryRuntime"
         }  
//        	standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
//			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
//		}
		standardTile("empty2x2", "null", width: 2, height: 2, decoration: "flat") {
             state "emptySmall", label:'', defaultState: true
        }
        
		main (["switch"])
			details(["switch", "battery" , "lastcheckin", "empty2x2", "batteryRuntime"]) //, "configure" , "refresh" , "lastcheckin" , "batteryVolts"
	}
}

def parse(String description) {
	 def result = zigbee.getEvent(description)

    if(result) {
        log.debug "${device.displayName}:ess - Parsing '${description}' Event Result: ${result}"
    }
    else
    {
        log.debug "${device.displayName}:ess - Parsing '${description}'"
    }
    //  send event for heartbeat
    def now = formatDate()    
    def nowDate = new Date(now).getTime()
    sendEvent(name: "lastCheckin", value: now)
    sendEvent(name: "lastCheckinDate", value: nowDate, displayed: false)

    Map map = [:]

    if (description?.startsWith('on/off: '))
    {
        map = parseCustomMessage(description)
        
    }
    else if (description?.startsWith('catchall:'))
    {
        map = parseCatchAllMessage(description)
    }
    else if (description?.startsWith("read attr - raw: "))
    {
        map = parseReadAttrMessage(description)
    }
    log.debug "${device.displayName}: Parse returned $map"
    def results = map ? createEvent(map) : null

    return results;
}

def configure(){
    log.debug "${device.displayName}: configuring"
    state.battery = 0
    checkIntervalEvent("configure");
    return
}

def refresh(){
	"st rattr 0x${device.deviceNetworkId} 1 2 0"
    	"st rattr 0x${device.deviceNetworkId} 1 0 0"
	log.debug "refreshing"
    	sendEvent(name: 'numberOfButtons', value: 1)
  
 }



private Map parseCatchAllMessage(String description) {
    def MsgLength
    def i
    Map resultMap = [:]
    def cluster = zigbee.parse(description)
    log.debug cluster
    if (cluster) {
        switch(cluster.clusterId) {
            case 0x0000:
            MsgLength = cluster.data.size();
            for (i = 0; i < (MsgLength-3); i++)
            {
                if ((cluster.data.get(i) == 0x01) && (cluster.data.get(i+1) == 0x21))  // check the data ID and data type
                {
                    // next two bytes are the battery voltage.
                    resultMap = getBatteryResult((cluster.data.get(i+3)<<8) + cluster.data.get(i+2))
                }
            }
            break
        }
    }
    return resultMap
}

private Map parseReadAttrMessage(String description) {
    def buttonRaw = (description - "read attr - raw:")
    Map resultMap = [:]

    def cluster = description.split(",").find {it.split(":")[0].trim() == "cluster"}?.split(":")[1].trim()
    def attrId = description.split(",").find {it.split(":")[0].trim() == "attrId"}?.split(":")[1].trim()
    def value = description.split(",").find {it.split(":")[0].trim() == "value"}?.split(":")[1].trim()
    def model = value.split("01FF")[0]
    def data = value.split("01FF")[1]
    log.debug "cluster: ${cluster}, attrId: ${attrId}, value: ${value}, model:${model}, data:${data}"

    if (data[4..7] == "0121") {
        def BatteryVoltage = (Integer.parseInt((data[10..11] + data[8..9]),16))
        resultMap = getBatteryResult(BatteryVoltage)
        log.debug "${device.displayName}: Parse returned $resultMap"
        createEvent(resultMap)
    }

    if (cluster == "0000" && attrId == "0005") {
        resultMap.name = 'Model'
        resultMap.value = ""
        resultMap.descriptionText = "device model"
        // Parsing the model
        for (int i = 0; i < model.length(); i+=2)
        {
            def str = model.substring(i, i+2);
            def NextChar = (char)Integer.parseInt(str, 16);
            resultMap.value = resultMap.value + NextChar
        }
        return resultMap
    }
    return [:]
}


private Map getBatteryResult(rawValue) {
	def rawVolts = rawValue / 1000
	def minVolts
    def maxVolts

    if(voltsmin == null || voltsmin == "")
    	minVolts = 2.5
    else
   	minVolts = voltsmin
    
    if(voltsmax == null || voltsmax == "")
    	maxVolts = 3.0
    else
	maxVolts = voltsmax
    
    def pct = (rawVolts - minVolts) / (maxVolts - minVolts)
    def roundedPct = Math.min(100, Math.round(pct * 100))

    def result = [
        name: 'battery',
        value: roundedPct,
        unit: "%",
        isStateChange:true,
        descriptionText : "${device.displayName} raw battery is ${rawVolts}v"
    ]

    log.debug "${device.displayName}: ${result}"
    //sendEvent(name: "battery", value: "${roundedPct}")
    //sendEvent(name: "batteryVolts", value: "${rawVolts}")
    return createEvent(result)
}

private Map parseCustomMessage(String description) {
	if (description?.startsWith('on/off: ')) {
    	if (description == 'on/off: 1') {		//button pushed
        //sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "$device.displayName button 1 was pushed", isStateChange: true)
    	return push()
        }
	}
}

//Need to reverse array of size 2
private byte[] reverseArray(byte[] array) {
    byte tmp;
    tmp = array[1];
    array[1] = array[0];
    array[0] = tmp;
    return array
}

private String swapEndianHex(String hex) {
    reverseArray(hex.decodeHex()).encodeHex()
}

def push() {
    def now = formatDate()
    def nowDate = new Date(now).getTime()
    sendEvent(name: "lastPress", value: now, displayed: false)
    sendEvent(name: "lastPressDate", value: nowDate, displayed: false) 
    sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "$device.displayName button 1 was pushed", isStateChange: true)
    
}

def on() {
	push()
}

def off() {
	push()
}

def formatDate(batteryReset) {
    def correctedTimezone = ""

    if (!(location.timeZone)) {
        correctedTimezone = TimeZone.getTimeZone("GMT")
        log.error "${device.displayName}: Time Zone not set, so GMT was used. Please set up your location in the SmartThings mobile app."
        sendEvent(name: "error", value: "", descriptionText: "ERROR: Time Zone not set, so GMT was used. Please set up your location in the SmartThings mobile app.")
    } 
    else {
        correctedTimezone = location.timeZone
    }
    if (dateformat == "US" || dateformat == "" || dateformat == null) {
        if (batteryReset)
            return new Date().format("MMM dd yyyy", correctedTimezone)
        else
            return new Date().format("EEE MMM dd yyyy h:mm:ss a", correctedTimezone)
    }
    else if (dateformat == "UK") {
        if (batteryReset)
            return new Date().format("dd MMM yyyy", correctedTimezone)
        else
            return new Date().format("EEE dd MMM yyyy h:mm:ss a", correctedTimezone)
        }
    else if (dateformat == "BG") {
        if (batteryReset)
            return new Date().format("dd MMM yyyy", correctedTimezone)
        else
            return new Date().format("EEE dd MMM yyyy HH:mm:ss", correctedTimezone)
        }        
    else {
        if (batteryReset)
            return new Date().format("dd MMM yyyy", correctedTimezone)
        else
            return new Date().format("EEE yyyy MMM dd HH:mm:ss", correctedTimezone)
    }
}

def resetBatteryRuntime() {
    def now = formatDate(true)   
    sendEvent(name: "batteryRuntime", value: now)
}

private checkIntervalEvent(text) {
    // Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline
    log.debug "${device.displayName}: Configured health checkInterval when ${text}()"
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}
/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
({
    testTypeDefDescriptor: {
        test: function(cmp) {
            var actual = cmp.getDef().getAttributeDefs().getDef("cmpDefRefs").getTypeDefDescriptor();
            $A.test.assertEquals("aura://Aura.ComponentDefRef[]", actual);
        }
    },

    testValueTypeInComponentDefRefArrayAttribute: {
        test: function(cmp) {
            var value = cmp.get("v.cmpDefRefs");
            $A.test.assertTrue($A.util.isArray(value),
                    "The value of ComponentDefRef[] Attribute should be an array");
            $A.test.assertEquals(1, value.length, "Missing an element in the array");
        }
    },

    /**
     * Verify that the elements in ComponentDefRef[] attribute don't get instantiated
     */
    testCmpInComponentDefRefArrayIsObjectIfUnused: {
        test: function(cmp){
            var value = cmp.get("v.cmpDefRefs");
            var element = value[0];

            $A.test.assertEquals("object", typeof element,
                    "The elements in ComponentDefRef[] should be type of object");
            $A.test.assertFalse($A.util.isComponent(element),
                    "The elements in ComponentDefRef[] should not be a component instance");
        }
    },

    /**
     * Verify that the elements in ComponentDefRef[] attribute get instantiated when they get rendered
     */
    testCmpInComponentDefRefArrayIsComponentIfRendered: {
        test: function(cmp){
            var value = cmp.get("v.cmpDefRefsOnFacet");
            var element = value[0];

            $A.test.assertTrue($A.util.isComponent(element), "The element should be a component instance");
        }
    },

    /**
     * Verify that the elements of ComponentDefRef[] attribute can be instantiated
     */
    testElementInComponentDefRefArrayAreCreatable: {
        test: function(cmp) {
            var element = cmp.get("v.cmpDefRefs")[0];

            var newCmp = $A.componentService.createComponentFromConfig(element);
            $A.test.assertEquals("uiButton", newCmp.getName(), "Created an unexpected component instance");
            $A.test.assertEquals("hi", newCmp.get("v.label"), "An attribute value is missing");
        }
    },

    testUnrenderComponentCreatedDuringRender: {
        test: [
            function(cmp) {
                var button = document.querySelector(".button");
                $A.test.assertNotUndefinedOrNull(button, "[Test Setup Failed] Expecting an ui:button gets rendered in DOM");
                // remove component to trigger unrender
                cmp.set("v.cmpDefRefsOnFacet", []);
            }, function(cmp) {
                var button = document.querySelector(".button");
                $A.test.assertNull(button, "The old button should be unrendered");
            }
        ]
    },

    testRerenderComponentInCmpDefRefArray: {
        test: [
            function(cmp) {
                var completed = false;
                $A.createComponent("markup://ui:button",
                    {
                        "label": "newButton",
                        "class": "newButton"
                    },
                    function(newCmp) {
                        var cmps = cmp.get("v.cmpDefRefsOnFacet");
                        cmps.push(newCmp);
                        // add new element to trigger rerender
                        cmp.set("v.cmpDefRefsOnFacet", cmps);
                        completed = true;
                    });

                $A.test.addWaitFor(true, function(){ return completed; } );
            }, function(cmp) {
                var button = document.querySelector(".button");
                $A.test.assertNotUndefinedOrNull(button, "The old element should get rerendered");

                var button = document.querySelector(".newButton");
                $A.test.assertNotUndefinedOrNull(button, "The new element doesn't get rendered");
            }
        ]
    },

    testUnrenderComponentInCmpDefRefArray: {
        test: [
            function(cmp) {
                var completed = false;
                $A.createComponent("markup://ui:button",
                    {
                        "label": "newButton",
                        "class": "newButton"
                    },
                    function(newCmp) {
                        var cmps = cmp.get("v.cmpDefRefsOnFacet");
                        cmps.push(newCmp);
                        cmp.set("v.cmpDefRefsOnFacet", cmps);
                        completed = true;
                    });

                $A.test.addWaitFor(true, function(){ return completed; });
            }, function(cmp) {
                var button = document.querySelector(".newButton");
                $A.test.assertNotUndefinedOrNull(button, "[Test Setup Failed] Expecting an ui:button in DOM");

                // remove component to trigger unrender
                cmp.set("v.cmpDefRefsOnFacet", []);
            }, function(cmp) {
                var button = document.querySelector(".newButton");
                $A.test.assertNull(button, "The added component should be unrendered");
            }
        ]
    }
})
